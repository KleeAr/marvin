package ar.com.klee.marvin.activities;



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
import android.widget.Toast;


import java.util.ArrayList;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.configuration.UserConfig;

public class ConfSpeedAlertActivity extends ActionBarActivity {

    private Switch switch1;
    private Toolbar toolbar;
    public TextView titleText;
    public TextView cityText;
    public ImageView weatherIconImageView;
    public TextView temperatureTextView;
    public TextView weekDay;
    public TextView dateText;

    private Spinner spinner1;
    private ArrayAdapter<String> arrayAdapter;
    private int chosenItem;
    private boolean isAlertEnabled = false;

    final ArrayList<String> speedVelocity = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_speed_alert);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       Typeface fBariolBold = Typeface.createFromAsset(getAssets(), "Bariol_Bold.otf");

        titleText = (TextView) findViewById(R.id.activityTitle);
        titleText.setVisibility(TextView.VISIBLE);
        titleText.setTypeface(fBariolBold);
        titleText.setText("Alerta de Velocidad");

        weekDay = (TextView) findViewById(R.id.weekDayText);
        dateText = (TextView) findViewById(R.id.dateText);
        cityText = (TextView) findViewById(R.id.cityText);
        temperatureTextView = (TextView) findViewById(R.id.temperatureText);
        weatherIconImageView = (ImageView) findViewById(R.id.weatherImage);
        weekDay.setVisibility(TextView.INVISIBLE);
        dateText.setVisibility(TextView.INVISIBLE);
        cityText.setVisibility(TextView.INVISIBLE);
        temperatureTextView.setVisibility(TextView.INVISIBLE);
        weatherIconImageView.setVisibility(ImageView.INVISIBLE);

        speedVelocity.add("40");
        speedVelocity.add("50");
        speedVelocity.add("60");
        speedVelocity.add("70");
        speedVelocity.add("80");
        speedVelocity.add("90");
        speedVelocity.add("100");
        speedVelocity.add("110");
        speedVelocity.add("120");
        speedVelocity.add("130");

        spinner1 = (Spinner) findViewById(R.id.spinner1);

        // Create the ArrayAdapter
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,speedVelocity);

        // Set the Adapter
        spinner1.setAdapter(arrayAdapter);

        chosenItem = UserConfig.getSettings().getAlertSpeed();
        int position = arrayAdapter.getPosition(((Integer)UserConfig.getSettings().getAlertSpeed()).toString());
        spinner1.setSelection(position);

        // Set the ClickListener for Spinner
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                chosenItem = Integer.parseInt(speedVelocity.get(i));

            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });

        switch1 = (Switch) findViewById(R.id.switch1);

        if(UserConfig.getSettings().isSpeedAlertEnabled())
            switch1.setChecked(true);
        else
            switch1.setChecked(false);

        isAlertEnabled = UserConfig.getSettings().isSpeedAlertEnabled();

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    isAlertEnabled = true;

                } else {

                    isAlertEnabled = false;

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


    public void saveSpeedAlert(View v){

        UserConfig.getSettings().setAlertSpeed(chosenItem);

        if(switch1.isChecked())
            UserConfig.getSettings().setSpeedAlertEnabled(true);
        else
            UserConfig.getSettings().setSpeedAlertEnabled(false);

        finish();

    }

    public void resetSpeedAlert(View v){

        chosenItem = arrayAdapter.getPosition("80");
        spinner1.setSelection(chosenItem);

        isAlertEnabled = false;
        switch1.setChecked(false);

    }

    public void cancelSpeedAlert(View v){

        finish();

    }
}