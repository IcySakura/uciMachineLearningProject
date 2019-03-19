<?php
// See detect_num_of_face.py for more info
// This php file will take 1 file as img and echo back the result

$target_path = "input/"; //here folder name 
$target_path = $target_path . basename($_FILES['file']['name']);

if(isset($_FILES['file'])){
    if(move_uploaded_file($_FILES['file']['tmp_name'], $target_path)) {
		echo "The file " . $_FILES['uploadedfile']['name'] .
       " has been uploaded";
       
        $msg_back = shell_exec("python3 detect_num_of_face.py ./input/" . $_FILES['uploadedfile']['name']);
        echo $msg_back;

	   } else {
	        echo "There was an error uploading the file, please try again!";
	 }
}

?>