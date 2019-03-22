<?php
// See FaceRecognizer.py for more info
// This php file will take 1 file as img and echo back the result
// Will take a parameter to see if it wants some sample data back
// when mode is 0, it is in normal mode, when mode is 1, it is in demo mode

if(isset($_FILES['file'])){
  
  $mode =  $_POST['mode'];
  
  if($mode == "0"){
    $target_path = "./input/faceRecognizerData/testData/"; //here folder name 
    $target_path = $target_path . basename($_FILES['file']['name']);

    //echo "Going to save to " . $target_path;
    if(move_uploaded_file($_FILES['file']['tmp_name'], $target_path)) {
        //echo "The file " . $_FILES['file']['name'] . " has been uploaded";
        //echo "Going to execute: ";
        //echo "python3 FaceRecognizer.py ./input/faceRecognizerData/testData/" . $_FILES['file']['name'];
        $msg_back = shell_exec("python3 FaceRecognizer.py ./input/faceRecognizerData/testData/" . $_FILES['file']['name']);
        echo $msg_back;
      } else {
          echo "There was an error uploading the file, please try again!";
    }
  } else {
    shell_exec("rm -r ./input/faceRecognizerData/trainData/s1;rm -r ./input/faceRecognizerData/trainData/s2");
    $target_path = "./input/faceRecognizerData/testData/"; //here folder name 
    $target_path = $target_path . basename($_FILES['file']['name']);
    shell_exec("cp -rp ./data/face_label_train_data/s1 ./input/faceRecognizerData/trainData/");
    shell_exec("cp -rp ./data/face_label_train_data/s2 ./input/faceRecognizerData/trainData/");

    //echo "Going to save to " . $target_path;
    if(move_uploaded_file($_FILES['file']['tmp_name'], $target_path)) {
        //echo "The file " . $_FILES['file']['name'] . " has been uploaded";
        //echo "Going to execute: ";
        //echo "python3 FaceRecognizer.py ./input/faceRecognizerData/testData/" . $_FILES['file']['name'];
        $msg_back = shell_exec("python3 FaceRecognizer.py ./input/faceRecognizerData/testData/" . $_FILES['file']['name']);
        echo $msg_back;
      } else {
          echo "There was an error uploading the file, please try again!";
    }
  }
}

?>