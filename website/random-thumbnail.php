<?php
    if (!defined('SITE_ROOT')) {
        define("SITE_ROOT", "./");
    }
    
    include_once(SITE_ROOT."admin/sim-utils.php");
    include_once(SITE_ROOT."admin/sys-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    
    $thumbnails = sim_get_animated_previews();
    
    if (count($thumbnails) > 0) {
		for ($i = 0; $i < count($thumbnails); $i++) {
        	$random_key = array_rand($thumbnails);

    		$animated_screenshot = $thumbnails[$random_key];

			if ($animated_screenshot_contents = file_get_contents($animated_screenshot)) {
				send_file_to_browser($animated_screenshot, $animated_screenshot_contents);
				
				break;
			}
		}
    }
	else {
		print "There are no animated previews.";
	}
?>