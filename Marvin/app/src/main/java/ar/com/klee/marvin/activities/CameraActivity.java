package ar.com.klee.marvin.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.camera.CameraDialog;
import ar.com.klee.marvin.camera.CameraPreview;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.Helper;

public class CameraActivity extends ActionBarActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private PictureCallback mPicture;
    private Button capture, save, share, saveAndShare, cancel;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    private byte[] datos;
    private File pictureFile;

    private CommandHandlerManager commandHandlerManager;
    private CameraDialog cameraDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;

        capture = (Button)findViewById(R.id.button_capture);
        save = (Button)findViewById(R.id.button_save);
        share = (Button)findViewById(R.id.button_share);
        saveAndShare = (Button)findViewById(R.id.button_saveAndShare);
        cancel = (Button)findViewById(R.id.button_cancel);

        save.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);
        saveAndShare.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);

        commandHandlerManager = Helper.commandHandlerManager;

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

        capture = (Button) findViewById(R.id.button_capture);
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

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    datos = stream.toByteArray();

                    save.setVisibility(View.VISIBLE);
                    saveAndShare.setVisibility(View.VISIBLE);
                    share.setVisibility(View.VISIBLE);
                    cancel.setVisibility(View.VISIBLE);

                }

            }
        };
        return picture;
    }

    public void saveAndShare(View v){

        save(v);
        share(v);

    }

    public void save(View v){

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

        mPreview.refreshCamera(mCamera);

        save.setVisibility(View.INVISIBLE);
        saveAndShare.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
        capture.setVisibility(View.VISIBLE);

        commandHandlerManager.setIsPhotoTaken(false);

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

        mPreview.refreshCamera(mCamera);

        save.setVisibility(View.INVISIBLE);
        saveAndShare.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
        capture.setVisibility(View.VISIBLE);

    }

    public void share(View v){

        cameraDialog = new CameraDialog(this, mPreview, mCamera);
        cameraDialog.show();

        save.setVisibility(View.INVISIBLE);
        saveAndShare.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
        capture.setVisibility(View.VISIBLE);

        commandHandlerManager.setIsPhotoTaken(false);

    }
    public void share(){

        cameraDialog = new CameraDialog(this, mPreview, mCamera);
        cameraDialog.show();

        save.setVisibility(View.INVISIBLE);
        saveAndShare.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
        capture.setVisibility(View.VISIBLE);

    }

    public void shareInFacebook(){

        cameraDialog.facebook();

    }

    public void shareInTwitter(){

        cameraDialog.twitter();

    }

    public void shareInInstagram(){

        cameraDialog.instagram();

    }

    public void cancel(View v){

        mPreview.refreshCamera(mCamera);

        save.setVisibility(View.INVISIBLE);
        saveAndShare.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
        capture.setVisibility(View.VISIBLE);

        commandHandlerManager.setIsPhotoTaken(false);

    }
    public void cancel(){

        mPreview.refreshCamera(mCamera);

        save.setVisibility(View.INVISIBLE);
        saveAndShare.setVisibility(View.INVISIBLE);
        share.setVisibility(View.INVISIBLE);
        cancel.setVisibility(View.INVISIBLE);
        capture.setVisibility(View.VISIBLE);

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
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "MV_" + timeStamp + ".jpg");

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