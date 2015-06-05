package ar.com.klee.marvin.camera;

import android.app.Activity;
import android.app.Dialog;
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

    public CameraDialog(Activity activity) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.activity = activity;
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
                showToast("Imagen compartida en Facebook");
                break;
            case R.id.btn_twt:
                showToast("Imagen compartida en Twitter");
                break;
            case R.id.btn_ins:
                showToast("Imagen compartida en Instagram");
                break;
            default:
                break;
        }
        dismiss();
    }

    public void showToast(String msj){

        Toast.makeText(activity, msj, Toast.LENGTH_LONG).show();

    }

}