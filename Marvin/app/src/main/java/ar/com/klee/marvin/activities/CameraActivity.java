package ar.com.klee.marvin.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.camera.CameraPreview;
import ar.com.klee.marvin.social.FacebookService;
import ar.com.klee.marvin.social.InstagramService;
import ar.com.klee.marvin.social.TwitterService;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;

public class CameraActivity extends ActionBarActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private PictureCallback mPicture;
    private ImageButton capture, save, share, saveAndShare, cancel;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    private byte[] datos;
    private File pictureFile;
    private TextView buttonInfo;

    private String lastImagePath;
    private Bitmap lastBitMap;
    private boolean onlyShare;

    private Dialog currentDialog;

    private CommandHandlerManager commandHandlerManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;

        capture = (ImageButton)findViewById(R.id.button_capture);
        save = (ImageButton)findViewById(R.id.button_save);
        share = (ImageButton)findViewById(R.id.button_share);
        saveAndShare = (ImageButton)findViewById(R.id.button_saveAndShare);
        cancel = (ImageButton)findViewById(R.id.button_cancel);
        buttonInfo = (TextView)findViewById(R.id.textView_buttonInfo);

        Typeface font = Typeface.createFromAsset(getAssets(), "Bariol_Bold.otf");
        buttonInfo.setTypeface(font);



        save.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        buttonInfo.setText("Guardar Foto");
                        break;
                    case MotionEvent.ACTION_UP:
                        buttonInfo.setText("");
                        save();
                        break;
                }
                return true;
            }
        });

        share.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        buttonInfo.setText("Compartir Foto");
                        break;
                    case MotionEvent.ACTION_UP:
                        buttonInfo.setText("");
                        share();
                        break;
                }
                return true;
            }
        });

        saveAndShare.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        buttonInfo.setText("Guardar y Compartir Foto");
                        break;
                    case MotionEvent.ACTION_UP:
                        buttonInfo.setText("");
                        saveAndShare();
                        break;
                }
                return true;
            }
        });

        cancel.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        buttonInfo.setText("Cancelar Foto");
                        break;
                    case MotionEvent.ACTION_UP:
                        buttonInfo.setText("");
                        cancel();
                        break;
                }
                return true;
            }
        });

        save.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);
        saveAndShare.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
        buttonInfo.setVisibility(View.INVISIBLE);

        commandHandlerManager = CommandHandlerManager.getInstance();

        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_CAMERA,this);

        initialize();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camara, menu);
        return true;
    }

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

    public void onBackPressed(){
        commandHandlerManager.setNullCommand();
        STTService.getInstance().setIsListening(false);
        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_MAIN, commandHandlerManager.getMainActivity());
        this.finish();
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //getString the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {
            //if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
            }
            mCamera = Camera.open(findBackFacingCamera());
            mPicture = getPictureCallback();
            mPreview.refreshCamera(mCamera);
        }
    }

    public void initialize() {
        cameraPreview = (LinearLayout) findViewById(R.id.camera_preview);
        mPreview = new CameraPreview(myContext, mCamera, this);
        cameraPreview.addView(mPreview);

        switchCamera();

        capture = (ImageButton) findViewById(R.id.button_capture);
        capture.setOnClickListener(captrureListener);

    }

    public void switchCamera() {
        //if the camera preview is the front
        if (cameraFront) {
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }

    private boolean hasCamera(Context context) {
        //check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    private PictureCallback getPictureCallback() {
        PictureCallback picture = new PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                capture.setVisibility(View.INVISIBLE);

                //make a new picture file
                pictureFile = getOutputMediaFile();

                lastImagePath = pictureFile.getPath();

                if (pictureFile == null) {
                    return;
                }

                if (data != null) {
                    int screenWidth = getResources().getDisplayMetrics().widthPixels;
                    int screenHeight = getResources().getDisplayMetrics().heightPixels;
                    Bitmap bm = BitmapFactory.decodeByteArray(data, 0, (data != null) ? data.length : 0);

                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        // Notice that width and height are reversed
                        Bitmap scaled = Bitmap.createScaledBitmap(bm, screenHeight, screenWidth, true);
                        int w = scaled.getWidth();
                        int h = scaled.getHeight();
                        // Setting post rotate to 90
                        Matrix mtx = new Matrix();
                        mtx.postRotate(270);
                        // Rotating Bitmap
                        bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
                    }else{// LANDSCAPE MODE
                        //No need to reverse width and height
                        Bitmap scaled = Bitmap.createScaledBitmap(bm, screenWidth,screenHeight , true);
                        bm=scaled;
                    }

                    lastBitMap = bm;

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    datos = stream.toByteArray();

                    save.setVisibility(View.VISIBLE);
                    saveAndShare.setVisibility(View.VISIBLE);
                    share.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.VISIBLE);
                    buttonInfo.setVisibility(View.VISIBLE);

                }

            }
        };
        return picture;
    }

    public void save(){

        try {
            //write the file
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(datos);
            fos.close();
            Toast toast = Toast.makeText(myContext, "Imagen guardada: " + pictureFile.getName(), Toast.LENGTH_LONG);
            toast.show();

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

        onlyShare = false;

        mPreview.refreshCamera(mCamera);
        commandHandlerManager.setIsPhotoTaken(false);

        save.setVisibility(View.INVISIBLE);
        saveAndShare.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
        capture.setVisibility(View.VISIBLE);
        buttonInfo.setVisibility(View.INVISIBLE);

    }

    public void share(){

        try {
            //write the file
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(datos);
            fos.close();
            Toast toast = Toast.makeText(myContext, "Imagen guardada: " + pictureFile.getName(), Toast.LENGTH_LONG);
            toast.show();

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

        onlyShare = true;

        openShareDialog();

        save.setVisibility(View.INVISIBLE);
        saveAndShare.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
        capture.setVisibility(View.VISIBLE);
        buttonInfo.setVisibility(View.INVISIBLE);

    }

    public void saveAndShare(){

        try {
            //write the file
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(datos);
            fos.close();
            Toast toast = Toast.makeText(myContext, "Imagen guardada: " + pictureFile.getName(), Toast.LENGTH_LONG);
            toast.show();

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

        onlyShare = false;

        openShareDialog();

        save.setVisibility(View.INVISIBLE);
        saveAndShare.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
        capture.setVisibility(View.VISIBLE);
        buttonInfo.setVisibility(View.INVISIBLE);

    }

    public void shareInFacebook(String text){

        FacebookService facebookService = new FacebookService(this);

        facebookService.postImage(lastBitMap, text);

        if(onlyShare){

            File photo = new File(lastImagePath);
            photo.delete();

        }

        currentDialog.dismiss();

    }

    public void shareInTwitter(String text){

        TwitterService twitterService = TwitterService.getInstance();

        twitterService.postTweet(text, new File(lastImagePath));

        if(onlyShare){

            File photo = new File(lastImagePath);
            photo.delete();

        }

        currentDialog.dismiss();

    }

    public void shareInInstagram(String text){

        InstagramService instagramService = new InstagramService(this);

        instagramService.postImageOnInstagram("image/png", text, lastImagePath);

        if(onlyShare){

            File photo = new File(lastImagePath);
            photo.delete();

        }

        currentDialog.dismiss();

    }

    public void cancel(){

        mPreview.refreshCamera(mCamera);

        commandHandlerManager.setIsPhotoTaken(false);

        save.setVisibility(View.INVISIBLE);
        saveAndShare.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
        capture.setVisibility(View.VISIBLE);
        buttonInfo.setVisibility(View.INVISIBLE);

    }

    public void closeDialog(){
        currentDialog.dismiss();
    }

    public  void openShareDialog(){
        final Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setCancelable(true);
        customDialog.setContentView(R.layout.dialog_camera);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        currentDialog = customDialog;

        customDialog.findViewById(R.id.facebook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                shareInFacebook("");

                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();
                customDialog.dismiss();

            }
        });
        customDialog.findViewById(R.id.twitter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                shareInTwitter("");

                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();
                customDialog.dismiss();

            }
        });
        customDialog.findViewById(R.id.instagram).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                shareInInstagram("");

                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();
                customDialog.dismiss();

            }
        });

        customDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    if(onlyShare){

                        File photo = new File(lastImagePath);
                        photo.delete();

                    }

                    STTService.getInstance().setIsListening(false);
                    commandHandlerManager.setNullCommand();
                    customDialog.dismiss();
                }
                return true;
            }

        });

        customDialog.show();

    }

    OnClickListener captrureListener = new OnClickListener() {
        public void onClick(View v) {

            commandHandlerManager.setIsPhotoTaken(true);

            mCamera.takePicture(null, null, mPicture);
        }
    };

    public void takePicture(){

        mCamera.takePicture(null, null, mPicture);

    }

    //make picture and save to a folder
    private static File getOutputMediaFile() {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File("/sdcard/", "MARVIN");

        //if this "JCGCamera folder does not exist
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "MV_" + timeStamp + ".png");

        return mediaFile;
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
}