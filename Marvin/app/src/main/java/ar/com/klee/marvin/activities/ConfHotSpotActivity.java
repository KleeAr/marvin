package ar.com.klee.marvin.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import ar.com.klee.marvin.R;


public class ConfHotSpotActivity extends ActionBarActivity {

    private String hotspotName = "MRVN"; //Nombre de la red creada
    private String hotspotPassword = "marvinHotSpot"; //Contraseña de la red creada

    private Switch switch1;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_hotspot);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getSupportActionBar().setTitle("CONFIGURACIÓN HOTSPOT");
        final EditText netName;
        final EditText netPassword;

        netName= (EditText) findViewById(R.id.netName);
        netPassword= (EditText) findViewById(R.id.netPassword);


        switch1 = (Switch) findViewById(R.id.switch1);

// set the switch to OFF
        switch1.setChecked(false);
// attach a listener to check for changes in state
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    Toast.makeText(getApplicationContext(), "Activado", Toast.LENGTH_SHORT).show();

                    hotspotName=netName.getText().toString();
                    hotspotPassword=netPassword.getText().toString();

                } else {

                    Toast.makeText(getApplicationContext(), "NO", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }
}
