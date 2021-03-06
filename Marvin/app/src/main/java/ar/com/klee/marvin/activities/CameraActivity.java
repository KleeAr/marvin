package ar.com.klee.marvin.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import ar.com.klee.marvin.client.model.User;
import ar.com.klee.marvin.configuration.UserConfig;
import ar.com.klee.marvin.social.FacebookService;
import ar.com.klee.marvin.social.InstagramService;
import ar.com.klee.marvin.social.TwitterService;
import ar.com.klee.marvin.social.exceptions.InstagramException;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;

public class CameraActivity extends ActionBarActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private PictureCallback mPicture;
    private ImageButton capture, save, share, cancel;
    private ImageView photo;
    private Context myContext;
    private FrameLayout cameraPreview;
    private boolean cameraFront = false;
    private byte[] datos;
    private File pictureFile;

    private String lastImagePath;
    private Bitmap lastBitMap;

    private boolean isSaved;

    private CommandHandlerManager commandHandlerManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(UserConfig.getInstance().getOrientation() == UserConfig.ORIENTATION_PORTRAIT)
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;

        capture = (ImageButton)findViewById(R.id.button_capture);
        save = (ImageButton)findViewById(R.id.button_save);
        share = (ImageButton)findViewById(R.id.button_share);
        cancel = (ImageButton)findViewById(R.id.button_cancel);

        photo = (ImageView)findViewById(R.id.photo);

        save.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);

        photo.setVisibility(View.INVISIBLE);

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
        if(commandHandlerManager.getIsPhotoTaken() && !isSaved) {
            File photo = new File(lastImagePath);
            photo.delete();
        }
        releaseCamera();
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

            if(commandHandlerManager.getIsPhotoTaken()) {

                capture.setVisibility(View.INVISIBLE);
                save.setVisibility(View.VISIBLE);
                share.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);

                mCamera = Camera.open(findFrontFacingCamera());
                mPicture = getPictureCallback();

            }else{

                capture.setVisibility(View.VISIBLE);
                save.setVisibility(View.INVISIBLE);
                share.setVisibility(View.INVISIBLE);
                cancel.setVisibility(View.INVISIBLE);

                mCamera = Camera.open(findFrontFacingCamera());
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);

            }

        }else{

            if(commandHandlerManager.getIsPhotoTaken()){
                cameraPreview.removeView(mPreview);
                photo.setImageBitmap(lastBitMap);
                photo.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                photo.setScaleType(ImageView.ScaleType.FIT_XY);
                photo.setVisibility(View.VISIBLE);
                releaseCamera();
            }else{
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    public void initialize() {
        cameraPreview = (FrameLayout) findViewById(R.id.camera_preview);
        mPreview = new CameraPreview(myContext, mCamera, this);
        cameraPreview.addView(mPreview);

        try {
            switchCamera();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "No se pudo establecer la conexión con la cámara", Toast.LENGTH_LONG).show();
            finish();
        }

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
        //releaseCamera();
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

                    if (UserConfig.getSettings().getOrientation() == UserConfig.ORIENTATION_PORTRAIT) {
                        // Notice that width and height are reversed
                        Bitmap scaled = Bitmap.createScaledBitmap(bm, screenHeight, screenWidth, true);
                        int w = scaled.getWidth();
                        int h = scaled.getHeight();
                        // Setting post rotate to 90
                        Matrix mtx = new Matrix();
                        mtx.postRotate(270);
                        mtx.preScale(1.0f, -1.0f);
                        // Rotating Bitmap
                        bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
                    }else{// LANDSCAPE MODE
                        //No need to reverse width and height
                        Bitmap scaled = Bitmap.createScaledBitmap(bm, screenWidth,screenHeight , true);
                        int w = scaled.getWidth();
                        int h = scaled.getHeight();
                        // Setting post rotate to 90
                        Matrix mtx = new Matrix();
                        mtx.preScale(1.0f, -1.0f);
                        // Rotating Bitmap
                        bm = Bitmap.createBitmap(scaled, 0, 0, w, h, mtx, true);
                    }

                    lastBitMap = bm;

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    datos = stream.toByteArray();

                    isSaved = false;
                    save.setVisibility(View.VISIBLE);
                    share.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.VISIBLE);

                }

            }
        };
        return picture;
    }

    public void save(View v){

        save();

    }

    public void save(){

        try {
            //write the file
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(datos);
            fos.close();
            isSaved = true;
            Toast toast = Toast.makeText(myContext, "Imagen guardada: " + pictureFile.getName(), Toast.LENGTH_LONG);
            toast.show();

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

    }

    public void share(){

        try {
            //write the file
            if(!isSaved) {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(datos);
                fos.close();
            }

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

    }

    public void shareInFacebook(String text){

        if(!text.equals("")){
            Character firstCharacter, newFirstCharacter;

            firstCharacter = text.charAt(0);
            newFirstCharacter = Character.toUpperCase(firstCharacter);
            text = text.replaceFirst(firstCharacter.toString(),newFirstCharacter.toString());

        }

        final FacebookService facebookService = new FacebookService(this);

        Handler handler = new Handler();
        final String finalText = text;
        handler.postDelayed(new Runnable() {
            public void run() {
                facebookService.postImage(lastBitMap, finalText);
            }
        }, 1000);

    }

    public void shareInTwitter(String text){

        if(!text.equals("")){
            Character firstCharacter, newFirstCharacter;

            firstCharacter = text.charAt(0);
            newFirstCharacter = Character.toUpperCase(firstCharacter);
            text = text.replaceFirst(firstCharacter.toString(),newFirstCharacter.toString());

        }

        final TwitterService twitterService = TwitterService.getInstance();

        Handler handler = new Handler();
        final String finalText = text;
        handler.postDelayed(new Runnable() {
            public void run() {
                twitterService.postTweet(finalText, new File(lastImagePath));
            }
        }, 1000);

    }

    public void shareInInstagram(String text){

        if(!text.equals("")){
            Character firstCharacter, newFirstCharacter;

            firstCharacter = text.charAt(0);
            newFirstCharacter = Character.toUpperCase(firstCharacter);
            text = text.replaceFirst(firstCharacter.toString(),newFirstCharacter.toString());

        }

        final InstagramService instagramService = new InstagramService(this);

        Handler handler = new Handler();
        final String finalText = text;
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    instagramService.postImageOnInstagram("image/png", finalText, lastImagePath);
                }catch (InstagramException e){
                    e.printStackTrace();
                    Toast.makeText(commandHandlerManager.getActivity(), "La aplicación Instagram no se encuentra instalada.", Toast.LENGTH_LONG).show();
                }
            }
        }, 1000);

    }

    public void cancel(View v){

        cancel();

    }

    public void cancel(){

        commandHandlerManager.setIsPhotoTaken(false);

        save.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
        capture.setVisibility(View.VISIBLE);

        if(photo.isShown()) {
            photo.setVisibility(View.INVISIBLE);
            mPreview = new CameraPreview(myContext, mCamera, this);
            cameraPreview.addView(mPreview);
            mCamera = Camera.open(findFrontFacingCamera());
            mPicture = getPictureCallback();
        }

        mPreview.refreshCamera(mCamera);

        if(!isSaved) {
            File photo = new File(lastImagePath);
            photo.delete();
        }

    }

    public  void openShareDialog(View v){

        AlertDialog.Builder builder =new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(true);
        builder.setTitle("¿Dónde querés compartir la foto?");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final ImageView imgMarvin = new ImageView(this);
        imgMarvin.setImageResource(R.drawable.marvin);
        imgMarvin.setMaxHeight(30);
        imgMarvin.setMaxWidth(30);
        layout.addView(imgMarvin);

        builder.setView(layout);

        builder.setPositiveButton("facebook", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                share();

                try {
                    shareInFacebook("");
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "No se pudo publicar en Facebook. Recordá asociar la cuenta en tu perfil.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();
                dialog.dismiss();

            }
        });
        builder.setNegativeButton("twitter", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                share();

                try {
                    shareInTwitter("");
                    Toast.makeText(getApplicationContext(), "Foto publicada en Twitter.", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "No se pudo publicar en Twitter. Recordá asociar la cuenta en tu perfil.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();
                dialog.dismiss();

            }
        });

        builder.setNeutralButton("instagram", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                share();

                try {
                    shareInInstagram("");
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "No se pudo publicar en Instagram. Reintentá.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                STTService.getInstance().setIsListening(false);
                commandHandlerManager.setNullCommand();
                dialog.dismiss();

            }
        });

        builder.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    File photo = new File(lastImagePath);
                    photo.delete();

                    STTService.getInstance().setIsListening(false);
                    commandHandlerManager.setNullCommand();
                   // customDialog.dismiss();
                }
                return true;
            }

        });


        builder.show();

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

    public void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public void reloadCamera(){
        mCamera = Camera.open(findFrontFacingCamera());
    }
}