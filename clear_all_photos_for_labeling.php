<<?php
// See FaceRecognizer.py for more info
// This php will delete all photos in the traindata

echo "Going to clear the trainData folder";
$msg_back = shell_exec("rm -r ./input/faceRecognizerData/trainData");

echo "Cleared with msg: " . $msg_back;

$old_umask = umask(0);
mkdir("./input/faceRecognizerData/trainData", 0777);
umask($old_umask);

?>