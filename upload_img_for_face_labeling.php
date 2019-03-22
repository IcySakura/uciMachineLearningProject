<?php
// See FaceRecognizer.py for more info
// This php file will take 1 file as img
// Will take a parameter as the label of the image
// Will take a parameter as the name of the image

if(isset($_FILES['file'])){

    //echo phpinfo();
    $label =  $_POST['label'];
    $img_name =  $_POST['img_name'];

    $old_umask = umask(0);
    mkdir("./input/faceRecognizerData/trainData/$label", 0777);
    umask($old_umask);

    $target_path = "./input/faceRecognizerData/trainData/$label/"; //here folder name 
    $target_path = $target_path . basename($img_name);

	//echo "Trying to get file: ".$_FILES['file']['name']." with: ".$_FILES['file']['tmp_name'];

    if(move_uploaded_file($_FILES['file']['tmp_name'], $target_path)) {
		echo "The file " . $_FILES['file']['name'] . " has been uploaded";

	   } else {
	        echo "There was an error uploading the file, please try again!";
	 }
}

?>