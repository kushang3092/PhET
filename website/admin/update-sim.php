<?php

    include_once("password-protection.php");
    include_once("db.inc");
    include_once("web-utils.php");
    include_once("sim-utils.php");
    include_once("db-utils.php");    

    $update_simulation_st = "UPDATE `simulation` SET ";
    
    gather_array_into_globals($_REQUEST);
    
    $is_first_entry = true; 

    // Update every field that was passed in as a _REQUEST parameter and 
    // which starts with 'sim_':
    foreach($_REQUEST as $key => $value) {
        if (preg_match('/sim_.*/', $key) == 1 && $key !== "sim_id") {
            $escaped_value = mysql_escape_string($value);

            if ($is_first_entry) {
                $is_first_entry = false;
            }
            else {
                $update_simulation_st = "$update_simulation_st, ";
            }
            
            $update_simulation_st = "$update_simulation_st `$key`='$escaped_value' ";
        }
    }
    
    // The sorting name should not be prefixed by such words as 'the', 'an', 'a':
    $sim_sorting_name = get_sorting_name($sim_name);
    
    $update_simulation_st = "$update_simulation_st, `sim_sorting_name`='$sim_sorting_name' ";

    // Specify which sim to update:
    $update_simulation_st = "$update_simulation_st WHERE `sim_id`='$sim_id' ";

    verify_mysql_result(mysql_query($update_simulation_st, $connection), $update_simulation_st);
    
    // Now we have ot update the categories manually:
    $select_categories_st = "SELECT * FROM `category` ";
    $category_rows        = mysql_query($select_categories_st, $connection);
    
    while ($category_row=mysql_fetch_array($category_rows)) {
        $cat_id   = $category_row[0];
        $cat_name = $category_row[1];
        
        $key_to_check_for = "checkbox_cat_id_$cat_id";

        $statement             = "";
        $should_be_in_category = isset($_REQUEST["$key_to_check_for"]);
               
        run_sql_statement("DELETE FROM `simulation_listing` WHERE `cat_id`='$cat_id' AND `sim_id`='$sim_id' ");
                
        if ($should_be_in_category) {
            run_sql_statement("INSERT INTO `simulation_listing` (`sim_id`, `cat_id`) VALUES('$sim_id', '$cat_id')");
        }
    }  
    
    print "The simulation \"$sim_name\" was successfully updated.";
    
    force_redirect("edit-sim.php?sim_id=$sim_id", 2);
?>