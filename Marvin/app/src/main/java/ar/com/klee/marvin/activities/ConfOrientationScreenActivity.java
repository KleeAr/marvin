package ar.com.klee.marvin.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import ar.com.klee.marvin.R;

public class ConfOrientationScreenActivity extends ActionBarActivity {

    private int orientation; //Orientación de la pantalla

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_orientation);

        getSupportActionBar().setTitle("FIJAR ROTACIÓN DE PANTALLA");

        RadioGroup rg = (RadioGroup)findViewById(R.id.radioGroup);
        String radiovalue = ((RadioButton)findViewById(rg.getCheckedRadioButtonId())).getText().toString();

        Toast.makeText(getApplicationContext(), radiovalue , Toast.LENGTH_SHORT).show();

    }
}
