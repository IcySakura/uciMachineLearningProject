<?php
// See detect_gender.py for more info
// This php file will take 1 file as img and echo back the result

if(isset($_FILES['file'])){

    //echo phpinfo();

    $target_path = "./input/"; //here folder name 
    $target_path = $target_path . basename($_FILES['file']['name']);

	//echo "Trying to get file: ".$_FILES['file']['name']." with: ".$_FILES['file']['tmp_name'];

    if(move_uploaded_file($_FILES['file']['tmp_name'], $target_path)) {
		//echo "The file " . $_FILES['file']['name'] . " has been uploaded";
       
        $msg_back = shell_exec("python3 detect_gender.py ./input/" . $_FILES['file']['name']);
        echo $msg_back;

	   } else {
	        echo "There was an error uploading the file, please try again!";
	 }
}

?>