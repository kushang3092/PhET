<?php
	
	
	function where_array($arr) {
		if(sizeof($arr) == 0) {
			return "";
		}
		if(sizeof($arr) == 1) {
			return " WHERE {$arr[0]}";
		}
		return " WHERE (" . join(" AND ", $arr) . ")";
	}
	
	function tables_array($arr) {
		if(sizeof($arr) == 0) {
			return "";
		}
		return ", " . join(", ", $arr);
	}
	
	function group_array($arr) {
		if(sizeof($arr) == 0) {
			return "";
		}
		return " GROUP BY " . join(", ", $arr);
	}
	
	function plain_cmp($val) {
		if($val == "null") {
			return " IS NULL";
		}
		if($val == "not_null") {
			return " IS NOT NULL";
		}
		if(substr($val, 0, 10) == "less_than:") {
			return " < " . substr($val, 10);
		}
		if(substr($val, 0, 16) == "less_than_equal:") {
			return " <= " . substr($val, 16);
		}
		if(substr($val, 0, 13) == "greater_than:") {
			return " > " . substr($val, 13);
		}
		if(substr($val, 0, 19) == "greater_than_equal:") {
			return " >= " . substr($val, 19);
		}
		return " = {$val}";
	}
	
	function string_equal($val) {
		if($val == "null") {
			return " IS NULL";
		}
		if($val == "not_null") {
			return " IS NOT NULL";
		}
		return " = '{$val}'";
	}
	
	function report_query($arr) {
		$query = array();
		$session_where = array();
		$tables = array();
		$group_by = array();
		$pre_select = "";
		$order_by = "";
		
		$flash_info = false;
		$java_info = false;
		
		$query_name = $arr['query'];
		
		if($arr['sim_type'] !== null) {
			if($arr['sim_type'] == "flash") {
				array_push($session_where, "session.sim_type != 0");
			} else if($arr['sim_type'] == "java") {
				array_push($session_where, "session.sim_type = 0");
			}
		}
		if($arr['sim_project'] !== null) {
			array_push($query, "SELECT (@pid := sim_project.id) FROM sim_project WHERE sim_project.name = '{$arr['sim_project']}'; ");
			array_push($session_where, "session.sim_project = @pid");
		}
		if($arr['sim_name'] !== null) {
			array_push($query, "SELECT (@sid := sim_name.id) FROM sim_name WHERE sim_name.name = '{$arr['sim_name']}'; ");
			array_push($session_where, "session.sim_name = @sid");
		}

		$plain_wheres = array(
		    "sim_dev" => "session",
		    "sim_major_version" => "session",
		    "sim_minor_version" => "session",
		    "sim_dev_version" => "session",
		    "sim_svn_revision" => "session",
		    "sim_version_timestamp" => "session",
		    "host_flash_version_major" => "session_flash_info",
		    "host_flash_version_minor" => "session_flash_info",
		    "host_flash_version_revision" => "session_flash_info",
		    "host_flash_version_build" => "session_flash_info",
		    "host_flash_time_offset" => "session_flash_info",
		    "host_flash_accessibility" => "session_flash_info"
		);
		foreach($plain_wheres as $field_name => $table_name) {
		    if($arr[$field_name] !== null) {
		        if($table_name == "session_flash_info") {
		            $flash_info = true;
                } else if($table_name == "session_java_info") {
                    $java_info = true;
                }
		        array_push($session_where, "{$table_name}.{$field_name}" . plain_cmp($arr[$field_name]));
            }
		}

		$string_wheres = array(
		    "sim_locale_language" => "session",
		    "sim_locale_country" => "session",
		    "sim_sessions_since" => "session",
		    "sim_total_sessions" => "session",
		    "host_locale_language" => "session",
		    "host_locale_country" => "session"
		);
		foreach($string_wheres as $field_name => $table_name) {
		    if($arr[$field_name] !== null) {
		        array_push($session_where, "{$table_name}.{$field_name}" . string_equal($arr[$field_name]));
            }
		}

		if($arr['sim_deployment'] !== null) {
			array_push($query, "SELECT (@deploy := deployment.id) FROM deployment WHERE deployment.name = '{$arr['sim_deployment']}'; ");
			array_push($session_where, "session.sim_deployment = @deploy");
		}
		if($arr['sim_distribution_tag'] !== null) {
			array_push($query, "SELECT (@dist_tag := distribution_tag.id) FROM distribution_tag WHERE distribution_tag.name = '{$arr['sim_distribution_tag']}'; ");
			array_push($session_where, "session.sim_distribution_tag = @dist_tag");
		}
		if($arr['host_simplified_os'] !== null) {
			array_push($query, "SELECT (@os := simplified_os.id) FROM simplified_os WHERE simplified_os.name = '{$arr['host_simplified_os']}'; ");
			array_push($session_where, "session.host_simplified_os = @os");
		}
		
		$host_flash_version_type = $arr['host_flash_version_type'];
		$host_flash_domain = $arr['host_flash_domain'];
		$host_flash_os = $arr['host_flash_os'];
		
		if($host_flash_version_type !== null) {
			$flash_info = true;
			array_push($query, "SELECT (@fl_ver_type := flash_version_type.id) FROM flash_version_type WHERE flash_version_type.name = '{$host_flash_version_type}'; ");
			array_push($session_where, "session_flash_info.host_flash_version_type = @fl_ver_type");
		}
		if($host_flash_domain !== null) {
			$flash_info = true;
			array_push($query, "SELECT (@fl_domain := flash_domain.id) FROM flash_domain WHERE flash_domain.name = '{$host_flash_domain}'; ");
			array_push($session_where, "session_flash_info.host_flash_domain = @fl_domain");
		}
		if($host_flash_os !== null) {
			$flash_info = true;
			array_push($query, "SELECT (@fl_os := flash_os.id) FROM flash_os WHERE flash_os.name = '{$host_flash_os}'; ");
			array_push($session_where, "session_flash_info.host_flash_os = @fl_os");
		}
		
		// TODO: add in java field equalities
		
		
		$group = $arr['group'];
		
		if($group) {
			switch($group) {
				case "week":
					$pre_select .= "YEARWEEK(session.timestamp) as week, ";
					array_push($group_by, "week");
					break;
				case "version":
					$pre_select .= "CONCAT(session.sim_major_version, '.', session.sim_minor_version, '.', session.sim_dev_version, ' (', session.sim_svn_revision, ')') AS version, ";
					array_push($group_by, "version");
					break;
				case "os":
					$pre_select .= "simplified_os.name as os, ";
					array_push($session_where, "session.host_simplified_os = simplified_os.id");
					array_push($tables, "simplified_os");
					array_push($group_by, "host_simplified_os");
					break;
				case "sim_type":
					$pre_select .= "IF(session.sim_type = 0, 'java', 'flash') AS sim_type, ";
					array_push($group_by, "sim_type");
					break;
				case "sim_name":
					$pre_select .= "sim_name.name as sim_name, ";
					array_push($session_where, "session.sim_name = sim_name.id");
					array_push($tables, "sim_name");
					array_push($group_by, "sim_name");
					break;
				case "sim_project":
					$pre_select .= "sim_project.name as sim_project, ";
					array_push($session_where, "session.sim_project = sim_project.id");
					array_push($tables, "sim_project");
					array_push($group_by, "sim_project");
					break;
				case "project_name":
					$pre_select .= "sim_project.name as sim_project, sim_name.name as sim_name, ";
					array_push($session_where, "session.sim_project = sim_project.id");
					array_push($session_where, "session.sim_name = sim_name.id");
					array_push($tables, "sim_project");
					array_push($tables, "sim_name");
					array_push($group_by, "sim_project");
					array_push($group_by, "sim_name");
					break;
				case "sim_deployment":
					$pre_select .= "deployment.name as sim_deployment, ";
					array_push($session_where, "session.sim_deployment = deployment.id");
					array_push($tables, "deployment");
					array_push($group_by, "sim_deployment");
					break;
				case "sim_distribution_tag":
					$pre_select .= "distribution_tag.name as sim_distribution_tag, ";
					array_push($session_where, "session.sim_distribution_tag = distribution_tag.id");
					array_push($tables, "distribution_tag");
					array_push($group_by, "sim_distribution_tag");
					break;
				case "host_simplified_os":
					$pre_select .= "simplified_os.name as host_simplified_os, ";
					array_push($session_where, "session.host_simplified_os = simplified_os.id");
					array_push($tables, "simplified_os");
					array_push($group_by, "host_simplified_os");
					break;
				case "host_flash_version_type":
					$flash_info = true;
					$pre_select .= "flash_version_type.name as host_flash_version_type, ";
					array_push($session_where, "session_flash_info.host_flash_version_type = flash_version_type.id");
					array_push($tables, "flash_version_type");
					array_push($group_by, "host_flash_version_type");
					break;
				case "host_flash_domain":
					$flash_info = true;
					$pre_select .= "flash_domain.name as host_flash_domain, ";
					array_push($session_where, "session_flash_info.host_flash_domain = flash_domain.id");
					array_push($tables, "flash_domain");
					array_push($group_by, "host_flash_domain");
					break;
				case "host_flash_os":
					$flash_info = true;
					$pre_select .= "flash_os.name as host_flash_os, ";
					array_push($session_where, "session_flash_info.host_flash_os = flash_os.id");
					array_push($tables, "flash_os");
					array_push($group_by, "host_flash_os");
					break;
				case "host_flash_version":
					$flash_info = true;
					$pre_select .= "CONCAT(session_flash_info.host_flash_version_major, ',', session_flash_info.host_flash_version_minor, ',', session_flash_info.host_flash_version_revision, ',', session_flash_info.host_flash_version_build) as host_flash_version, ";
					array_push($group_by, "host_flash_version");
					break;
				case "host_java_os_name":
					$java_info = true;
					$pre_select .= "java_os_name.name as host_java_os_name, ";
					array_push($session_where, "session_java_info.host_java_os_name = java_os_name.id");
					array_push($tables, "java_os_name");
					array_push($group_by, "host_java_os_name");
					break;
				case "host_java_os_version":
					$java_info = true;
					$pre_select .= "java_os_version.name as host_java_os_version, ";
					array_push($session_where, "session_java_info.host_java_os_version = java_os_version.id");
					array_push($tables, "java_os_version");
					array_push($group_by, "host_java_os_version");
					break;
				case "host_java_os_arch":
					$java_info = true;
					$pre_select .= "java_os_arch.name as host_java_os_arch, ";
					array_push($session_where, "session_java_info.host_java_os_arch = java_os_arch.id");
					array_push($tables, "java_os_arch");
					array_push($group_by, "host_java_os_arch");
					break;
				case "host_java_os":
					$java_info = true;
					$pre_select .= "CONCAT(java_os_name.name, ' ', java_os_version.name, ' (', java_os_arch.name, ')') as host_java_os, ";
					array_push($session_where, "session_java_info.host_java_os_name = java_os_name.id");
					array_push($session_where, "session_java_info.host_java_os_version = java_os_version.id");
					array_push($session_where, "session_java_info.host_java_os_arch = java_os_arch.id");
					array_push($tables, "java_os_name");
					array_push($tables, "java_os_version");
					array_push($tables, "java_os_arch");
					array_push($group_by, "host_java_os_name");
					array_push($group_by, "host_java_os_version");
					array_push($group_by, "host_java_os_arch");
					break;
				case "host_java_vendor":
					$java_info = true;
					$pre_select .= "java_vendor.name as host_java_vendor, ";
					array_push($session_where, "session_java_info.host_java_vendor = java_vendor.id");
					array_push($tables, "java_vendor");
					array_push($group_by, "host_java_vendor");
					break;
				case "host_java_webstart_version":
					$java_info = true;
					$pre_select .= "java_webstart_version.name as host_java_webstart_version, ";
					array_push($session_where, "session_java_info.host_java_webstart_version = java_webstart_version.id");
					array_push($tables, "java_webstart_version");
					array_push($group_by, "host_java_webstart_version");
					break;
				case "host_java_timezone":
					$java_info = true;
					$pre_select .= "java_timezone.name as host_java_timezone, ";
					array_push($session_where, "session_java_info.host_java_timezone = java_timezone.id");
					array_push($tables, "java_timezone");
					array_push($group_by, "host_java_timezone");
					break;
				default:
					if(substr($group, 0, 10) == "host_flash") {
						$flash_info = true;
						$pre_select .= "session_flash_info.{$group} as {$group}, ";
						array_push($group_by, $group);
					} else if(substr($group, 0, 9) == "host_java") {
						$java_info = true;
						$pre_select .= "session_java_info.{$group} as {$group}, ";
						array_push($group_by, $group);
					} else {
						$pre_select .= "session.{$group} as {$group}, ";
						array_push($group_by, $group);
					}
			}
		}
		
		$order = $arr['order'];
		
		if($order) {
			if(substr($order, 0, 5) == "desc:") {
				$order_by = " ORDER BY " . substr($order, 5) . " DESC";
			} else {
				$order_by = " ORDER BY {$order}";
			}
		}
		
		$limit = $arr['limit'];
		if($order && $limit) {
			$order_by .= " LIMIT {$limit}";
		}
		
		
		if($flash_info) {
			array_push($session_where, "session.id = session_flash_info.session_id");
			array_push($tables, "session_flash_info");
		}
		if($java_info) {
			array_push($session_where, "session.id = session_java_info.session_id");
			array_push($tables, "session_java_info");
		}
		
		$session_where = where_array($session_where);
		$tables = tables_array($tables);
		$group_by = group_array($group_by);
		
		switch($query_name) {
			case "message_count":
				array_push($query, "SELECT {$pre_select}COUNT(*) as message_count FROM session{$tables}{$session_where}{$group_by}{$order_by}; ");
				break;
			case "session_count":
				array_push($query, "SELECT {$pre_select}SUM(session.sim_sessions_since) as session_count FROM session{$tables}{$session_where}{$group_by}{$order_by}; ");
				break;
			case "sim_type":
				if($arr['sim_name']) {
					array_push($query, "SELECT DISTINCT IF(sim_type = 0, 'java', 'flash') AS sim_type FROM session WHERE sim_name = @sid; ");
				} else { die("cannot have sim_type query without sim_name"); }
				break;
		}
		
		return $query;
	}
	
	function report_result($arr) {
		$queries = report_query($arr);
		$result = null;
		foreach($queries as $query) {
			$result = phet_mysql_query($query);
		}
		return $result;
	}
	
	function report_single_value($arr) {
		$result = report_result($arr);
		$row = mysql_fetch_row($result);
		return $row[0];
	}
	
	function report_table($desc, $args) {
		return "{$desc} [<a href='query-table.php?{$args}'>table</a>] [<a href='query-csv.php?{$args}'>csv</a>]<br />";
	}
?>