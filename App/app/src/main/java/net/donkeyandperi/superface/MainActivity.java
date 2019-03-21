package net.donkeyandperi.superface;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity ";

    private MyApp myApp;
    private Context context = this;
    private ProgressDialog progressDialog;

    private RadioGroup radioGroupFirst;
    private RadioButton numOfFaceInImageRadioButton;
    private RadioButton genderDetectionRadioButton;
    private Button takePhotoButton;
    private Button takePhotoButtonForLabeling;
    private Button clearAllLabelPhotoButton;
    private RadioGroup radioGroupSecond;
    private RadioButton uploadLabelS1Photo;
    private RadioButton uploadLabelS2Photo;
    private RadioButton predictWithNormal;
    private RadioButton predictWithSample;

    private int labelS1Counter = 1;
    private int labelS2Counter = 1;

    private String latestInfoFromServer = "";

    private Handler handler;
    private Handler handlerForDownload;

    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myApp = (MyApp)getApplication();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        initActivity();

        numOfFaceInImageRadioButton.setChecked(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initActivity(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(getString(R.string.talking_with_server));
        progressDialog.setCancelable(false);
        setUpHandler();
        setUpHandlerForDownload();
        numOfFaceInImageRadioButton = findViewById(R.id.content_superface_playground_detect_num_of_face);
        genderDetectionRadioButton = findViewById(R.id.content_superface_playground_detect_gender);
        radioGroupFirst = findViewById(R.id.content_superface_playground_radioGroup);
        takePhotoButton = findViewById(R.id.content_superface_playground_take_photo_button);
        takePhotoButtonForLabeling = findViewById(R.id.content_superface_labeling_take_photo_button);
        clearAllLabelPhotoButton = findViewById(R.id.content_superface_labeling_clear_all_photos_button);
        radioGroupSecond = findViewById(R.id.content_superface_labeling_radioGroup);
        uploadLabelS1Photo = findViewById(R.id.content_superface_labeling_upload_s1);
        uploadLabelS2Photo = findViewById(R.id.content_superface_labeling_upload_s2);
        predictWithNormal = findViewById(R.id.content_superface_normal_predict);
        predictWithSample = findViewById(R.id.content_superface_sample_predict);
        setUpTakePhotoButton();
        clearAllLabelPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myApp.setCurrentMode(2);
                new Connection(context, myApp).new ClearAllLabelPhotos(handler).execute();
                progressDialog.show();
            }
        });
    }

    private void setUpTakePhotoButton(){
        // Take photo button for superface playground
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();    // Going to let the user take a photo and back.
            }
        });
        // Take photo button for superface labeling
        takePhotoButtonForLabeling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(predictWithSample.isChecked()){
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle(getString(R.string.be_notified));
                    alertDialog.setMessage(getString(R.string.info_for_face_labeling));
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dispatchTakePictureIntent();
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    dispatchTakePictureIntent();
                }
            }
        });
    }

    private void setUpHandler(){
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                progressDialog.dismiss();
                boolean isGoingToShowAlertDialog = true;
                Bundle dataBack = msg.getData();
                if(dataBack == null){
                    return false;
                }
                if(dataBack.getBoolean("is_success")){
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle(getString(R.string.msg_from_server));
                    switch (myApp.getCurrentMode()){
                        case 0:
                            if(numOfFaceInImageRadioButton.isChecked()){
                                alertDialog.setMessage(String.format(getString(R.string.amount_of_face_detected),
                                        dataBack.getString("msg_from_server")));
                            } else if (genderDetectionRadioButton.isChecked()){
                                latestInfoFromServer = dataBack.getString("msg_from_server");
                                new Connection(context, myApp).new DownloadFile("gender_detection.jpg",
                                        handlerForDownload).execute();
                                isGoingToShowAlertDialog = false;
                                progressDialog.show();
                            }
                            break;
                        case 1:
                            if(uploadLabelS1Photo.isChecked() || uploadLabelS2Photo.isChecked()){
                                alertDialog.setMessage(getString(R.string.upload_success));
                            } else {
                                String msgBack = dataBack.getString("msg_from_server");
                                if(msgBack.equals("NULL")){
                                    alertDialog.setMessage(getString(R.string.no_similar_face_detected));
                                } else {
                                    msgBack = msgBack.substring(1, msgBack.length() - 2);
                                    String[] msgBackArray = msgBack.split(",");
                                    alertDialog.setMessage(String.format(getString(R.string.face_labeling_msg),
                                            msgBackArray[0], String.valueOf(100.0 - Float.valueOf(msgBackArray[1]))));
                                }
                            }
                            break;
                        case 2:
                            alertDialog.setMessage(getString(R.string.clear_success));
                            break;
                    }
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    if(isGoingToShowAlertDialog){
                        alertDialog.show();
                    }
                }
                if(myApp.getCurrentMode() == 2){
                    // Go Back to mode 1
                    myApp.setCurrentMode(1);
                }
                return false;
            }
        });
    }

    private void setUpHandlerForDownload(){
        handlerForDownload = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                progressDialog.dismiss();
                switch (myApp.getCurrentMode()){
                    case 0:
                        if(genderDetectionRadioButton.isChecked()){
                            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                            alertDialog.setTitle(getString(R.string.be_notified));
                            LayoutInflater factory = LayoutInflater.from(context);
                            final View view = factory.inflate(R.layout.alertdialog_with_image, null);
                            ImageView imageView = view.findViewById(R.id.dialog_imageview);
                            imageView.setImageBitmap(getGenderDetectionResultImage());
                            alertDialog.setView(view);
                            alertDialog.setCancelable(false);
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                        break;
                    case 1:
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        LinearLayout linearLayout;
        switch (myApp.getCurrentMode()){
            case 0:
                linearLayout = findViewById(R.id.content_main_superface_playground);
                linearLayout.setVisibility(View.GONE);
                break;
            case 1:
                linearLayout = findViewById(R.id.content_main_superface_labeling);
                linearLayout.setVisibility(View.GONE);
                break;
        }

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.face_playground) {
            myApp.setCurrentMode(0);
            linearLayout = findViewById(R.id.content_main_superface_playground);
            linearLayout.setVisibility(View.VISIBLE);
            numOfFaceInImageRadioButton.setChecked(true);
        } else if (id == R.id.face_labeling) {
            myApp.setCurrentMode(1);
            linearLayout = findViewById(R.id.content_main_superface_labeling);
            linearLayout.setVisibility(View.VISIBLE);
            uploadLabelS1Photo.setChecked(true);
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(getString(R.string.be_notified));
            alertDialog.setMessage(getString(R.string.info_for_face_labeling));
            alertDialog.setCancelable(false);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = myApp.createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "net.donkeyandperi.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            progressDialog.show();
            Log.d(TAG, "onActivityResult: What is selected here: " + radioGroupFirst.getCheckedRadioButtonId());
            switch (myApp.getCurrentMode()){
                case 0:
                    if(numOfFaceInImageRadioButton.isChecked()){
                        Log.d(TAG, "onActivityResult: Going to execute detectNumOfFace");
                        new Connection(context, myApp).new DetectNumOfFace(handler).execute();
                    } else if (genderDetectionRadioButton.isChecked()){
                        new Connection(context, myApp).new DetectGender(handler).execute();
                    }
                    break;
                case 1:
                    Log.d(TAG, "onActivityResult: Going to execute faceLabeling");
                    if(uploadLabelS1Photo.isChecked()){
                       new Connection(context, myApp).new UploadLabelPhoto(handler, "s1", labelS1Counter++).execute();
                    } else if (uploadLabelS2Photo.isChecked()){
                        new Connection(context, myApp).new UploadLabelPhoto(handler, "s2", labelS2Counter++).execute();
                    } else if (predictWithNormal.isChecked()){
                        new Connection(context, myApp).new FaceLabeling(handler, "0").execute();
                    } else if (predictWithSample.isChecked()){
                        new Connection(context, myApp).new FaceLabeling(handler, "1").execute();
                    }
                    break;
            }

        }
    }

    public Bitmap getGenderDetectionResultImage(){
        File imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                + "/" + "gender_detection.jpg");
        return BitmapFactory.decodeFile(imageFile.getPath());
    }

}
