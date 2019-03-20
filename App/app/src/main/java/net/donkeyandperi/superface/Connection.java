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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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

        public FaceLabeling(Handler handler){
            this.handler = handler;
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

}
