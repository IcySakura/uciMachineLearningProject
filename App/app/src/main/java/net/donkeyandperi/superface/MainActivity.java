package net.donkeyandperi.superface;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
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
    private Button takePhotoButton;
    private Button takePhotoButtonForLabeling;

    private Handler handler;

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
        setUpHandler();
        numOfFaceInImageRadioButton = findViewById(R.id.content_superface_playground_detect_num_of_face);
        radioGroupFirst = findViewById(R.id.content_superface_playground_radioGroup);
        takePhotoButton = findViewById(R.id.content_superface_playground_take_photo_button);
        takePhotoButtonForLabeling = findViewById(R.id.content_superface_labeling_take_photo_button);
        setUpTakePhotoButton();
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
                dispatchTakePictureIntent();
            }
        });
    }

    private void setUpHandler(){
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                progressDialog.dismiss();
                Bundle dataBack = msg.getData();
                if(dataBack == null){
                    return false;
                }
                if(dataBack.getBoolean("is_success")){
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle(getString(R.string.msg_from_server));
                    switch (myApp.getCurrentMode()){
                        case 0:
                            alertDialog.setMessage(String.format(getString(R.string.amount_of_face_detected),
                                    dataBack.getString("msg_from_server")));
                            break;
                        case 1:
                            String msgBack = dataBack.getString("msg_from_server");
                            if(msgBack.equals("NULL")){
                                alertDialog.setMessage(getString(R.string.no_similar_face_detected));
                            } else {
                                msgBack = msgBack.substring(1, msgBack.length() - 2);
                                String[] msgBackArray = msgBack.split(",");
                                alertDialog.setMessage(String.format(getString(R.string.face_labeling_msg),
                                        msgBackArray[0], msgBackArray[1].substring(1)));
                            }
                            break;
                    }
                    alertDialog.setCancelable(false);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
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
        } else if (id == R.id.face_labeling) {
            myApp.setCurrentMode(1);
            linearLayout = findViewById(R.id.content_main_superface_labeling);
            linearLayout.setVisibility(View.VISIBLE);
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
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(getString(R.string.talking_with_server));
            progressDialog.setCancelable(false);
            progressDialog.show();
            Log.d(TAG, "onActivityResult: What is selected here: " + radioGroupFirst.getCheckedRadioButtonId());
            switch (myApp.getCurrentMode()){
                case 0:
                    if(numOfFaceInImageRadioButton.isChecked()){
                        Log.d(TAG, "onActivityResult: Going to execute detectNumOfFace");
                        new Connection(context, myApp).new DetectNumOfFace(handler).execute();
                    }
                    break;
                case 1:
                    Log.d(TAG, "onActivityResult: Going to execute faceLabeling");
                    new Connection(context, myApp).new FaceLabeling(handler).execute();
                    break;
            }

        }
    }

}
