package ar.com.klee.marvin.activities;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.applications.Application;
import ar.com.klee.marvin.fragments.MainMenuFragment;


public class ConfFavoriteAppActivity extends ActionBarActivity {

    private Switch switch1;
    private ArrayList<String> listApp;

    private boolean openAppWhenStop = false; //Indica si está activada la opción de abrir una app al detenerse
    private String appToOpenWhenStop; //Indica el nombre de la aplicación a abrir al detenerse


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_favorite_app);

       // getSupportActionBar().setTitle("APLICACIÓN FAVORITA");


        listApp = new ArrayList<String>();
        listApp.add("Ninguna");


        int i = 0;

        while(i<12){
            if(!MainMenuFragment.shortcutList[i].getName().equals(""))
                listApp.add( MainMenuFragment.shortcutList[i].getName());
            i++;
        }


        Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);

        // Create the ArrayAdapter
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listApp);
        // Set the Adapter
        spinner1.setAdapter(arrayAdapter);
        // Set the ClickListener for Spinner
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "Seleccionaste: " + listApp.get(i), Toast.LENGTH_SHORT).show();
                appToOpenWhenStop="APLCIACION SELECCIONADA";

            }

            // If no option selected
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }

        });



        switch1 = (Switch) findViewById(R.id.switch1);

// set the switch to OFF
        switch1.setChecked(false);
// attach a listener to check for changes in state
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    openAppWhenStop = true;
                    Toast.makeText(getApplicationContext(), "Activado", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(getApplicationContext(), "NO", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }
}
