package net.donkeyandperi.superface;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
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

public class Connection {

    private static final String mainUriTag = "http://45.32.67.159/superface/";
    private static final String TAG = "Connection ";

    private Context sContext;
    private MyApp myApp;

    public Connection(Context context, MyApp myApp){
        sContext = context;
        this.myApp = myApp;
    }

    public class UploadPublicKey extends AsyncTask<String, Void, String> {

        public UploadPublicKey(){
            Log.d("UploadPublicKey ", " initialized...");
        }

        @Override
        protected String doInBackground(String... params) {
            File imageFile = new File(sContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "demo_img.jpg");

            HttpPost httppost = new HttpPost(mainUriTag + "detect_num_of_face.php");
            HttpClient myClient = new DefaultHttpClient();

            MultipartEntity entity = new MultipartEntity();
            StringBody x = null;
            try {
                x = new StringBody("demo_img.jpg", Charset.forName("UTF-8"));
                entity.addPart("title", x);
                FileBody fileBody = new FileBody(imageFile);
                entity.addPart("file", fileBody);
                httppost.setEntity(entity);
                httppost.getParams().setParameter("project", 1);
                HttpResponse myResponse = myClient.execute(httppost);

                BufferedReader br = new BufferedReader( new InputStreamReader(myResponse.getEntity().getContent()));
                String line = "";
                while ((line = br.readLine()) != null)
                {
                    Log.d("Myles: ", line);
                }
            }
            catch(UnsupportedEncodingException e) {
                Log.d("Myles: ", "Error");
                e.printStackTrace();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            return "";
        }
    }

}
