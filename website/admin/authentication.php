<?php
	if (!defined('SITE_ROOT')) {
    	include_once('../admin/global.php');
	}

    include_once(SITE_ROOT."admin/site-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/contrib-utils.php");

	$script = get_self_url();
    
    function print_first_time_login_form() {   
        print '<h1>Login</h1>';
        
		global $script;

		print_contribute_login_form($script, null, $script);
    }

    function print_retry_login_form() {        
        print '<h1>Login Incorrect</h1>';
        
        global $script;

		print_contribute_login_form($script, null, $script,
            "<p><strong>The password you entered is incorrect.</strong> If you entered the correct email address, please check your email now for a password reminder.</p>
            <p>If you don't have an account on the PhET website, please create a new account.</p>");
    }

    function print_not_an_email_login_form() {  
	    print '<h1>Invalid Email</h1>';
	
		global $script;
		
		print_contribute_login_form($script, null, $script,
            "<p><strong>The email address you entered is not a valid email address.</strong></p>
             <p>If you don't have an account on the PhET website, please create a new account.</p>");
    }    

    function print_empty_password_login_form() {
        print '<h1>No Password Specified</h1>';
        
		global $script;
		
		print_contribute_login_form($script, null, $script,
                         "<p><strong>You forgot to specify a password for your new account.</strong></p>
                         <p>Please specify a password now.</p>");
    }    

    function do_authentication($login_required = true) {
        static $already_tried_login_required = null;
        global $contributor_authenticated;

		if (isset($_REQUEST['login_required']) && $_REQUEST['login_required'] == "true") {
			$login_required = true;
		}

        if ($login_required === $already_tried_login_required) {
            return $contributor_authenticated;
        }

        $already_tried_login_required = $login_required;
        
        $contributor_authenticated = false;
        
        // Look for cookie variables:
        if (cookie_var_is_stored("username")) {
            $username      = cookie_var_get("username");
            $password_hash = cookie_var_get("password_hash");
            
            // Don't trust the cookie; validate it:
            if (!contributor_is_valid_login($username, $password_hash)) {
                // Cookie is invalid. Clear it:
                cookie_var_clear("username");
                cookie_var_clear("password_hash");

                if ($login_required) {
                    force_redirect(get_self_url(), 0);

                    exit;
                }
            }
            else {
                $contributor_authenticated = true;
            }
        }
        else {
            // No cookie variables
            if (isset($_REQUEST['username'])) {
                $username = $_REQUEST['username'];
            }
            else if (isset($_REQUEST['contributor_email'])) {
                $username = $_REQUEST['contributor_email'];

				$GLOBALS['username'] = $username;
            }
        
            if ((!isset($username) || $username == '') && isset($_REQUEST['contributor_name'])) {
                // Username not present, but contributor name is. 
                // Deduce email from contributor name:
                $contributor = contributor_get_contributor_by_name($_REQUEST['contributor_name']);
            
                if ($contributor) {
                    $username = $contributor['contributor_email'];
                }
            }
        
            if (isset($_REQUEST['password'])) {
                $password = $_REQUEST['password'];
            }
            else if (isset($_REQUEST['contributor_password'])) {
                $password = $_REQUEST['contributor_password'];

				$GLOBALS['password'] = $password;
            }

            if (!isset($username) || !isset($password)) {   
                // No username/password specified, and no cookie variables.
                // Print the first-time login form:      
                if ($login_required) {
                    print_site_page('print_first_time_login_form', 3);

                    exit;
                }
            }
            else {
                // Both username and password were specified.
                if (contributor_is_contributor($username)) {
                    // The username already exists and denotes a contributor. Check 
                    // the password to make sure it is correct.
        
                    $password_hash = md5($password);
        
                    if (!contributor_is_valid_login($username, $password_hash)) {
                        contributor_send_password_reminder($username);

                        if ($login_required) {
                            print_site_page('print_retry_login_form', 3);        

                            exit;
                        }
                    }
                    else {
                        cookie_var_store("username",      $username);
                        cookie_var_store("password_hash", $password_hash);
            
                        $contributor_authenticated = true;
                    }
                }
                else if (is_email($username)) {
                    // The username does not exist, and is a valid e-mail address.
                    if ($password == '') {
                        if ($login_required) {
                            print_site_page('print_empty_password_login_form', 3);
            
                            exit;
                        }
                    }
                    else {
                        // Create new user account:
                        $contributor_id = contributor_add_new_contributor($username, $password);
            
                        // Check for optional fields that may have been passed along:
                        if (isset($_REQUEST['contributor_name'])) {
                            contributor_update_contributor(
                                $contributor_id,
                                array(
                                    'contributor_name' => $_REQUEST['contributor_name']
                                )
                            );
                        }
            
                        if (isset($_REQUEST['contributor_organization'])) {
                            contributor_update_contributor(
                                $contributor_id,
                                array(
                                    'contributor_organization' => $_REQUEST['contributor_organization']
                                )
                            );
                        }

						if (isset($_REQUEST['contributor_desc'])) {
							contributor_update_contributor(
								$contributor_id,
								array(
									'contributor_desc' => $_REQUEST['contributor_desc']
								)
							);
						}
            
                        // Store the information in a cookie so user won't have to re-login:
                        cookie_var_store("username",      $username);
                        cookie_var_store("password_hash", md5($password));
            
                        $contributor_authenticated = true;
                    }
                }
                else {
                    // The username does not exist, nor is it a valid email address.
                    if ($login_required) {
                        print_site_page('print_not_an_email_login_form', 3);

                        exit;
                    }
                }
            }
        }

        if ($contributor_authenticated) {
            // Store the contributor id globally so the including script can use it:
            $contributor_id = contributor_get_id_from_username($username);

            // Stuff all the contributor fields into global variables:
            gather_array_into_globals(contributor_get_contributor_by_id($contributor_id));
        }
        
        return $contributor_authenticated;
    }
?>