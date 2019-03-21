<?php
// See predict_mood.py for more info
// This php file will take 1 file as img and echo back the result

/*
if (extension_loaded('gd')) {
    echo "<br>GD support is loaded ";
} else{
    echo "<br>GD support is NOT loaded ";
}*/

function resize_image($file, $w, $h, $crop=false) {
    list($width, $height) = getimagesize($file);
    $r = $width / $height;
    if ($crop) {
        if ($width > $height) {
            $width = ceil($width-($width*abs($r-$w/$h)));
        } else {
            $height = ceil($height-($height*abs($r-$w/$h)));
        }
        $newwidth = $w;
        $newheight = $h;
    } else {
        if ($w/$h > $r) {
            $newwidth = $h*$r;
            $newheight = $h;
        } else {
            $newheight = $w/$r;
            $newwidth = $w;
        }
    }
    
    //Get file extension
    $exploding = explode(".",$file);

    $ext = end($exploding);

    switch($ext){
        case "png":
            $src = imagecreatefrompng($file);
        break;
        case "jpeg":
        case "jpg":
            $src = imagecreatefromjpeg($file);
        break;
        case "gif":
            $src = imagecreatefromgif($file);
        break;
        default:
            $src = imagecreatefromjpeg($file);
        break;
    }
    
    $dst = imagecreatetruecolor($newwidth, $newheight);
    imagecopyresampled($dst, $src, 0, 0, 0, 0, $newwidth, $newheight, $width, $height);

    return $dst;
}

if(isset($_FILES['file'])){

    //echo phpinfo();

    $target_path = "./input/"; //here folder name 
    $target_path = $target_path . basename($_FILES['file']['name']);

	//echo "Trying to get file: ".$_FILES['file']['name']." with: ".$_FILES['file']['tmp_name'];

    if(move_uploaded_file($_FILES['file']['tmp_name'], $target_path)) {
        //echo "The file " . $_FILES['file']['name'] . " has been uploaded";
        
        $filename = $target_path;
        $resizedFilename = "./input/resized_demo.png";

        //echo "Going to resize from " . $filename . " to " . $resizedFilename;

        $imgData = resize_image($filename, 1920, 1080);

        imagepng($imgData, $resizedFilename);

        $msg_back = shell_exec("python3 predict_mood.py " . $resizedFilename);
        echo $msg_back;

	   } else {
	        echo "There was an error uploading the file, please try again!";
	 }
}

?>