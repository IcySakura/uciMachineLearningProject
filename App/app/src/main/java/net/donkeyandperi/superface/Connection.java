package net.donkeyandperi.superface;

import android.content.Context;
import android.gesture.Prediction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Connection {

    private static final String mainUriTag = "http://45.32.67.159/superface/";
    private static final String TAG = "Connection ";

    private Context sContext;
    private MyApp myApp;

    public Connection(Context context, MyApp myApp){
        sContext = context;
        this.myApp = myApp;
    }

    public class DetectNumOfFace extends AsyncTask<String, Void, String> {

        private Handler handler;

        public DetectNumOfFace(Handler handler){
            this.handler = handler;
            Log.d("DetectNumOfFace ", " initialized...");
        }

        @Override
        protected String doInBackground(String... params) {
            File imageFile = new File(myApp.getNumOfFaceDetectionImageUri().toString());

            HttpPost httppost = new HttpPost(mainUriTag + "detect_num_of_face.php");
            HttpClient myClient = new DefaultHttpClient();

            MultipartEntity entity = new MultipartEntity();
            StringBody x = null;
            Message msg = new Message();
            try {
                Log.d(TAG, "DetectNumOfFace: Comparing " + myApp.getNumOfFaceDetectionImageName()
                        + " with " + myApp.getNumOfFaceDetectionImageUri().toString());
                x = new StringBody(myApp.getNumOfFaceDetectionImageName(), Charset.forName("UTF-8"));
                entity.addPart("title", x);
                FileBody fileBody = new FileBody(imageFile);
                entity.addPart("file", fileBody);
                httppost.setEntity(entity);
                httppost.getParams().setParameter("project", 1);
                HttpResponse myResponse = myClient.execute(httppost);

                BufferedReader br = new BufferedReader( new InputStreamReader(myResponse.getEntity().getContent()));
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = br.readLine()) != null)
                {
                    stringBuilder.append(line);
                    Log.d("DetectNumOfFace: ", line);
                }
                Bundle bundle = new Bundle();
                bundle.putString("msg_from_server", stringBuilder.toString());
                bundle.putBoolean("is_success", true);
                msg.setData(bundle);
            }
            catch(UnsupportedEncodingException e) {
                Log.d("DetectNumOfFace: ", "Error");
                e.printStackTrace();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            handler.sendMessage(msg);
            return "";
        }
    }

    public class FaceLabeling extends AsyncTask<String, Void, String> {

        private Handler handler;
        private String mode;

        public FaceLabeling(Handler handler, String mode){
            this.handler = handler;
            this.mode = mode;
            Log.d("FaceLabeling ", " initialized...");
        }

        @Override
        protected String doInBackground(String... params) {
            File imageFile = new File(myApp.getNumOfFaceDetectionImageUri().toString());

            HttpPost httppost = new HttpPost(mainUriTag + "FaceRecognizer.php");
            HttpClient myClient = new DefaultHttpClient();

            MultipartEntity entity = new MultipartEntity();
            StringBody x = null;
            Message msg = new Message();
            try {
                Log.d(TAG, "FaceLabeling: Comparing " + myApp.getNumOfFaceDetectionImageName()
                        + " with " + myApp.getNumOfFaceDetectionImageUri().toString());
                x = new StringBody(myApp.getNumOfFaceDetectionImageName(), Charset.forName("UTF-8"));
                entity.addPart("title", x);
                FileBody fileBody = new FileBody(imageFile);
                entity.addPart("file", fileBody);
                entity.addPart("mode", new StringBody(mode));
                httppost.setEntity(entity);
                httppost.getParams().setParameter("project", 1);
                HttpResponse myResponse = myClient.execute(httppost);

                BufferedReader br = new BufferedReader( new InputStreamReader(myResponse.getEntity().getContent()));
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = br.readLine()) != null)
                {
                    if(line.contains("Prediction:")){
                        try {
                            stringBuilder.append(line.split("Prediction:")[1]);
                        } catch (IndexOutOfBoundsException e){
                            stringBuilder.append("NULL");
                        }
                    }
                    Log.d("FaceLabeling: ", line);
                }
                Bundle bundle = new Bundle();
                bundle.putString("msg_from_server", stringBuilder.toString());
                bundle.putBoolean("is_success", true);
                msg.setData(bundle);
            }
            catch(UnsupportedEncodingException e) {
                Log.d("FaceLabeling: ", "Error");
                e.printStackTrace();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            handler.sendMessage(msg);
            return "";
        }
    }

    public class UploadLabelPhoto extends AsyncTask<String, Void, String> {

        private Handler handler;
        private String label;
        private int counter;

        public UploadLabelPhoto(Handler handler, String label, int counter){
            this.handler = handler;
            this.label = label;
            this.counter = counter;
            Log.d("UploadLabelPhoto ", " initialized...");
        }

        @Override
        protected String doInBackground(String... params) {
            File imageFile = new File(myApp.getNumOfFaceDetectionImageUri().toString());

            HttpPost httppost = new HttpPost(mainUriTag + "upload_img_for_face_labeling.php");
            HttpClient myClient = new DefaultHttpClient();

            MultipartEntity entity = new MultipartEntity();
            StringBody x = null;
            Message msg = new Message();
            try {
                Log.d(TAG, "UploadLabelPhoto: Comparing " + myApp.getNumOfFaceDetectionImageName()
                        + " with " + myApp.getNumOfFaceDetectionImageUri().toString());
                x = new StringBody(myApp.getNumOfFaceDetectionImageName(), Charset.forName("UTF-8"));
                entity.addPart("title", x);
                FileBody fileBody = new FileBody(imageFile);
                entity.addPart("file", fileBody);
                entity.addPart("label", new StringBody(label));
                entity.addPart("img_name", new StringBody(String.valueOf(counter) + ".jpg"));
                httppost.setEntity(entity);
                httppost.getParams().setParameter("project", 1);
                HttpResponse myResponse = myClient.execute(httppost);

                BufferedReader br = new BufferedReader( new InputStreamReader(myResponse.getEntity().getContent()));
                String line = "";
                Bundle bundle = new Bundle();
                while ((line = br.readLine()) != null)
                {
                    if(line.contains("has been uploaded")){
                        bundle.putBoolean("is_success", true);
                    }
                    Log.d("UploadLabelPhoto: ", line);
                }
                msg.setData(bundle);
            }
            catch(UnsupportedEncodingException e) {
                Log.d("UploadLabelPhoto: ", "Error");
                e.printStackTrace();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            handler.sendMessage(msg);
            return "";
        }
    }

    public class ClearAllLabelPhotos extends AsyncTask<String, Void, String> {

        private Handler handler;

        public ClearAllLabelPhotos(Handler handler){
            this.handler = handler;
            Log.d("ClearAllLabelPhotos ", " initialized...");
        }

        @Override
        protected String doInBackground(String... params) {

            HttpPost httppost = new HttpPost(mainUriTag + "clear_all_photos_for_labeling.php");
            HttpClient myClient = new DefaultHttpClient();

            MultipartEntity entity = new MultipartEntity();
            StringBody x = null;
            Message msg = new Message();
            try {
                httppost.setEntity(entity);
                httppost.getParams().setParameter("project", 1);
                HttpResponse myResponse = myClient.execute(httppost);

                BufferedReader br = new BufferedReader( new InputStreamReader(myResponse.getEntity().getContent()));
                String line = "";
                Bundle bundle = new Bundle();
                while ((line = br.readLine()) != null)
                {
                    Log.d("ClearAllLabelPhotos: ", line);
                }
                bundle.putBoolean("is_success", true);
                msg.setData(bundle);
            }
            catch(UnsupportedEncodingException e) {
                Log.d("ClearAllLabelPhotos: ", "Error");
                e.printStackTrace();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            handler.sendMessage(msg);
            return "";
        }
    }

    public class DownloadFile extends AsyncTask<String, Void, String> {

        private String filename;
        private Handler handler;
        private String dir;

        public DownloadFile(String filename, String dir, Handler handler){
            this.filename = filename;
            this.handler = handler;
            this.dir = dir;
        }

        @Override
        protected String doInBackground(String... params) {
            String url_str = mainUriTag + "/output/" + dir + filename;
            Bundle bundle = new Bundle();
            try {
                URL url = new URL(url_str);
                Log.d("DownloadFile: ", "Connected to " + url_str);
                int count;
                try {
                    Log.d("DownloadFile: ", "Downdload background");

                    URLConnection conection = url.openConnection();
                    conection.connect();
                    // getting file length
                    int lenghtOfFile = conection.getContentLength();

                    // input stream to read file - with 8k buffer
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);
                    // Output stream to write file
                    File dir = new File(sContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                            + "/" );
                    dir.mkdirs();
                    File outputFile = new File(dir, filename);

                    OutputStream output = new FileOutputStream(outputFile);
                    byte data[] = new byte[1024];

                    long total = 0;
                    while ((count = input.read(data)) != -1) {
                        total += count;

                        // writing data to file
                        output.write(data, 0, count);
                    }
                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();

                    bundle.putBoolean("is_success", true);

                    Log.d(TAG, "DownloadFile: " + filename + "has been downloaded to " + dir.getAbsolutePath());

                } catch (Exception e) {
                    bundle.putBoolean("is_success", false);
                    Log.e("DownloadFile Error: ", e.getMessage());
                }
            } catch (Exception e) {
                bundle.putBoolean("is_success", false);
                Log.d("DownloadFile: Error", "try");
            }
            Message message = new Message();
            message.setData(bundle);
            handler.sendMessage(message);
            return "";
        }
    }

    public class DetectGender extends AsyncTask<String, Void, String> {

        private Handler handler;

        public DetectGender(Handler handler){
            this.handler = handler;
            Log.d("DetectGender ", " initialized...");
        }

        @Override
        protected String doInBackground(String... params) {
            File imageFile = new File(myApp.getNumOfFaceDetectionImageUri().toString());

            HttpPost httppost = new HttpPost(mainUriTag + "detect_gender.php");
            HttpClient myClient = new DefaultHttpClient();

            MultipartEntity entity = new MultipartEntity();
            StringBody x = null;
            Message msg = new Message();
            try {
                Log.d(TAG, "DetectGender: Comparing " + myApp.getNumOfFaceDetectionImageName()
                        + " with " + myApp.getNumOfFaceDetectionImageUri().toString());
                x = new StringBody(myApp.getNumOfFaceDetectionImageName(), Charset.forName("UTF-8"));
                entity.addPart("title", x);
                FileBody fileBody = new FileBody(imageFile);
                entity.addPart("file", fileBody);
                httppost.setEntity(entity);
                httppost.getParams().setParameter("project", 1);
                HttpResponse myResponse = myClient.execute(httppost);

                BufferedReader br = new BufferedReader( new InputStreamReader(myResponse.getEntity().getContent()));
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = br.readLine()) != null)
                {
                    stringBuilder.append(line);
                    Log.d("DetectGender: ", line);
                }
                Bundle bundle = new Bundle();
                bundle.putString("msg_from_server", stringBuilder.toString());
                bundle.putBoolean("is_success", true);
                msg.setData(bundle);
            }
            catch(UnsupportedEncodingException e) {
                Log.d("DetectGender: ", "Error");
                e.printStackTrace();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            handler.sendMessage(msg);
            return "";
        }
    }

    public class DetectMood extends AsyncTask<String, Void, String> {

        private Handler handler;

        public DetectMood(Handler handler){
            this.handler = handler;
            Log.d("DetectMood ", " initialized...");
        }

        @Override
        protected String doInBackground(String... params) {
            File imageFile = new File(myApp.getNumOfFaceDetectionImageUri().toString());

            HttpPost httppost = new HttpPost(mainUriTag + "detect_mood.php");
            HttpClient myClient = new DefaultHttpClient();

            MultipartEntity entity = new MultipartEntity();
            StringBody x = null;
            Message msg = new Message();
            try {
                Log.d(TAG, "DetectMood: Comparing " + myApp.getNumOfFaceDetectionImageName()
                        + " with " + myApp.getNumOfFaceDetectionImageUri().toString());
                x = new StringBody(myApp.getNumOfFaceDetectionImageName(), Charset.forName("UTF-8"));
                entity.addPart("title", x);
                FileBody fileBody = new FileBody(imageFile);
                entity.addPart("file", fileBody);
                httppost.setEntity(entity);
                httppost.getParams().setParameter("project", 1);
                HttpResponse myResponse = myClient.execute(httppost);

                BufferedReader br = new BufferedReader( new InputStreamReader(myResponse.getEntity().getContent()));
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = br.readLine()) != null)
                {
                    stringBuilder.append(line);
                    Log.d("DetectMood: ", line);
                }
                Bundle bundle = new Bundle();
                bundle.putString("msg_from_server", stringBuilder.toString());
                bundle.putBoolean("is_success", true);
                msg.setData(bundle);
            }
            catch(UnsupportedEncodingException e) {
                Log.d("DetectMood: ", "Error");
                e.printStackTrace();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            handler.sendMessage(msg);
            return "";
        }
    }

    public class DetectObject extends AsyncTask<String, Void, String> {

        private Handler handler;

        public DetectObject(Handler handler){
            this.handler = handler;
            Log.d("DetectObject ", " initialized...");
        }

        @Override
        protected String doInBackground(String... params) {
            File imageFile = new File(myApp.getNumOfFaceDetectionImageUri().toString());

            HttpPost httppost = new HttpPost(mainUriTag + "object_detection.php");
            HttpClient myClient = new DefaultHttpClient();

            MultipartEntity entity = new MultipartEntity();
            StringBody x = null;
            Message msg = new Message();
            try {
                Log.d(TAG, "DetectObject: Comparing " + myApp.getNumOfFaceDetectionImageName()
                        + " with " + myApp.getNumOfFaceDetectionImageUri().toString());
                x = new StringBody(myApp.getNumOfFaceDetectionImageName(), Charset.forName("UTF-8"));
                entity.addPart("title", x);
                FileBody fileBody = new FileBody(imageFile);
                entity.addPart("file", fileBody);
                httppost.setEntity(entity);
                httppost.getParams().setParameter("project", 1);
                HttpResponse myResponse = myClient.execute(httppost);

                BufferedReader br = new BufferedReader( new InputStreamReader(myResponse.getEntity().getContent()));
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = br.readLine()) != null)
                {
                    stringBuilder.append(line);
                    Log.d("DetectObject: ", line);
                }
                Bundle bundle = new Bundle();
                bundle.putString("msg_from_server", stringBuilder.toString());
                bundle.putBoolean("is_success", true);
                msg.setData(bundle);
            }
            catch(UnsupportedEncodingException e) {
                Log.d("DetectObject: ", "Error");
                e.printStackTrace();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            handler.sendMessage(msg);
            return "";
        }
    }

}
