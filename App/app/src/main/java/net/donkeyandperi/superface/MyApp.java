package net.donkeyandperi.superface;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyApp extends Application {

    private static final String TAG = "MyApp";
    private int mode = 0;   // 0 means we are in face playground; 1 means we are in face labeling

    public File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = getNumOfFaceDetectionImageName();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName);
        /*
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        */
        return image;
    }

    public String getNumOfFaceDetectionImageName(){
        return "demo" + ".jpg";
    }

    public Uri getNumOfFaceDetectionImageUri(){
        return Uri.parse(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + getNumOfFaceDetectionImageName());
    }

    public Bitmap getCurrentUserPhoto(){
        File imageFile = new File(getNumOfFaceDetectionImageUri().toString());
        Log.d(TAG, "getCurrentUserPhoto: the path is: " + imageFile.getAbsolutePath() + " with the size of: " + imageFile.getTotalSpace());
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
        Log.d(TAG, "getCurrentUserPhoto: isItNull: " + bitmap);
        return bitmap;
    }

    public void setCurrentMode(int mode){
        this.mode = mode;
    }

    public int getCurrentMode(){
        return mode;
    }

}
