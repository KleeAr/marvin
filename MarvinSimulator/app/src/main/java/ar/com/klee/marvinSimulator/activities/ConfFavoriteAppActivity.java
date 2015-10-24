package ar.com.klee.marvinSimulator.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import ar.com.klee.marvinSimulator.configuration.UserConfig;
import ar.com.klee.marvinSimulator.fragments.MainMenuFragment;


public class ConfFavoriteAppActivity extends ActionBarActivity {

    private Switch switch1;
    private Spinner spinner1;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listApp;
    private int chosenApp;
    private boolean switchEnabled = false;

    private Toolbar toolbar;
    public TextView titleText;
    public TextView cityText;
    public ImageView weatherIconImageView;
    public TextView temperatureTextView;
    public TextView weekDay;
    public TextView dateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ar.com.klee.marvinSimulator.R.layout.activity_configure_favorite_app);

        toolbar = (Toolbar) findViewById(ar.com.klee.marvinSimulator.R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Typeface fBariolBold = Typeface.createFromAsset(getAssets(), "Bariol_Bold.otf");

        titleText = (TextView) findViewById(ar.com.klee.marvinSimulator.R.id.activityTitle);
        titleText.setVisibility(TextView.VISIBLE);
        titleText.setTypeface(fBariolBold);
        titleText.setText("Aplicación Favorita");

        weekDay = (TextView) findViewById(ar.com.klee.marvinSimulator.R.id.weekDayText);
        dateText = (TextView) findViewById(ar.com.klee.marvinSimulator.R.id.dateText);
        cityText = (TextView) findViewById(ar.com.klee.marvinSimulator.R.id.cityText);
        temperatureTextView = (TextView) findViewById(ar.com.klee.marvinSimulator.R.id.temperatureText);
        weatherIconImageView = (ImageView) findViewById(ar.com.klee.marvinSimulator.R.id.weatherImage);
        weekDay.setVisibility(TextView.INVISIBLE);
        dateText.setVisibility(TextView.INVISIBLE);
        cityText.setVisibility(TextView.INVISIBLE);
        temperatureTextView.setVisibility(TextView.INVISIBLE);
        weatherIconImageView.setVisibility(ImageView.INVISIBLE);


        listApp = new ArrayList<String>();
        listApp.add("Ninguna");

        int i = 0;
        boolean appConfigured = false;

        while(i<12){
            if(!MainMenuFragment.shortcutList[i].getName().equals("")) {
                listApp.add(MainMenuFragment.shortcutList[i].getName());
                if(MainMenuFragment.shortcutList[i].getName().equals(UserConfig.getSettings().getAppToOpenWhenStop()))
                    appConfigured = true;
            }
            i++;
        }


        spinner1 = (Spinner) findViewById(ar.com.klee.marvinSimulator.R.id.spinner1);

        // Create the ArrayAdapter
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listApp);
        // Set the Adapter
        spinner1.setAdapter(arrayAdapter);

        if(appConfigured){
            chosenApp = arrayAdapter.getPosition(UserConfig.getSettings().getAppToOpenWhenStop());
            spinner1.setSelection(chosenApp);
        }else {
            chosenApp = arrayAdapter.getPosition("Ninguna");
            spinner1.setSelection(chosenApp);
        }

        // Set the ClickListener for Spinner
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(getApplicationContext(), "Seleccionaste: " + listApp.get(i), Toast.LENGTH_SHORT).show();
                chosenApp = i;
            }

            // If no option selected
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }

        });

        switch1 = (Switch) findViewById(ar.com.klee.marvinSimulator.R.id.switch1);

        if(UserConfig.getSettings().isOpenAppWhenStop())
            switch1.setChecked(true);
        else
            switch1.setChecked(false);

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    switchEnabled = true;
                } else {
                    switchEnabled = false;
                }

            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                //app icon in action bar clicked
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveFavoriteApp(View v){

        UserConfig.getSettings().setAppToOpenWhenStop(listApp.get(chosenApp));

        if (switchEnabled) {
            UserConfig.getSettings().setOpenAppWhenStop(true);
        } else {
            UserConfig.getSettings().setOpenAppWhenStop(false);
        }

        finish();

    }

    public void resetFavoriteApp(View v){

        switch1.setChecked(false);

        chosenApp = arrayAdapter.getPosition("Ninguna");
        spinner1.setSelection(chosenApp);

    }

    public void cancelFavoriteApp(View v){

        finish();

    }
}
