package ar.com.klee.marvin.camera;

import android.app.Activity;
import android.app.Dialog;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import ar.com.klee.marvin.R;

public class CameraDialog extends Dialog implements
        android.view.View.OnClickListener  {

    public Activity activity;
    public Button fcb, twt, ins;
    private Camera camera;
    private CameraPreview cameraPreview;

    public CameraDialog(Activity activity, CameraPreview mPreview, Camera mCamera) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.activity = activity;

        camera = mCamera;
        cameraPreview = mPreview;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_camera);

        fcb = (Button) findViewById(R.id.btn_fcb);
        twt = (Button) findViewById(R.id.btn_twt);
        ins = (Button) findViewById(R.id.btn_ins);

        fcb.setOnClickListener(this);
        twt.setOnClickListener(this);
        ins.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_fcb:
                facebook();
                break;
            case R.id.btn_twt:
                twitter();
                break;
            case R.id.btn_ins:
                instagram();
                break;
            default:
                break;
        }

    }

    public void facebook(){

        showToast("Imagen compartida en Facebook");

        //refresh camera to continue preview
        cameraPreview.refreshCamera(camera);

        dismiss();

    }

    public void twitter(){

        showToast("Imagen compartida en Twitter");

        //refresh camera to continue preview
        cameraPreview.refreshCamera(camera);

        dismiss();

    }

    public void instagram(){

        showToast("Imagen compartida en Instagram");

        //refresh camera to continue preview
        cameraPreview.refreshCamera(camera);

        dismiss();

    }

    public void showToast(String msj){

        Toast.makeText(activity, msj, Toast.LENGTH_LONG).show();

    }

}