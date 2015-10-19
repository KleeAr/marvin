package ar.com.klee.marvin.activities;

import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.configuration.UserConfig;

public class ConfHistoryTripActivity extends ActionBarActivity {

    private Toolbar toolbar;
    public TextView titleText;
    public TextView cityText;
    public ImageView weatherIconImageView;
    public TextView temperatureTextView;
    public TextView weekDay;
    public TextView dateText;

    private long timeChosenValue;
    private long distanceChosenValue;

    private ArrayAdapter<String> arrayAdapter1;
    private ArrayAdapter<String> arrayAdapter2;
    private Spinner spinner1;
    private Spinner spinner2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(UserConfig.getSettings().getOrientation() == UserConfig.ORIENTATION_PORTRAIT)
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        setContentView(R.layout.activity_configure_history_trip);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Typeface fBariolBold = Typeface.createFromAsset(getAssets(), "Bariol_Bold.otf");

        titleText = (TextView) findViewById(R.id.activityTitle);
        titleText.setVisibility(TextView.VISIBLE);
        titleText.setTypeface(fBariolBold);
        titleText.setText("Historial de Viajes");

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

        final ArrayList<String> timeMin = new ArrayList<String>();
        timeMin.add("5");
        timeMin.add("15");
        timeMin.add("30");
        timeMin.add("60");

        final ArrayList<String> tripMin = new ArrayList<String>();
        tripMin.add("1");
        tripMin.add("5");
        tripMin.add("10");
        tripMin.add("15");
        tripMin.add("20");

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);

        // Create the ArrayAdapter

        arrayAdapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,timeMin);
        arrayAdapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,tripMin);


        // Set the Adapter
        spinner1.setAdapter(arrayAdapter1);
        spinner2.setAdapter(arrayAdapter2);

        timeChosenValue = UserConfig.getSettings().getMiniumTripTime();
        int position = arrayAdapter1.getPosition(UserConfig.getSettings().getMiniumTripTime().toString());
        spinner1.setSelection(position);

        distanceChosenValue = UserConfig.getSettings().getMiniumTripDistance();
        position = arrayAdapter2.getPosition(UserConfig.getSettings().getMiniumTripDistance().toString());
        spinner2.setSelection(position);

        // Set the ClickListener for Spinner
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                timeChosenValue = Integer.parseInt(timeMin.get(i));

            }

            // If no option selected
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });

        // Set the ClickListener for Spinner
        spinner2.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView,View view, int i, long l) {

                distanceChosenValue = Integer.parseInt(tripMin.get(i));

            }
            // If no option selected
            public void onNothingSelected(AdapterView<?> arg0) {

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

    public void saveHistoryTrip(View v){

        UserConfig.getSettings().setMiniumTripTime(timeChosenValue);
        UserConfig.getSettings().setMiniumTripDistance(distanceChosenValue);

        finish();

    }

    public void resetHistoryTrip(View v){

        timeChosenValue = 30;
        int position = arrayAdapter1.getPosition("30");
        spinner1.setSelection(position);

        distanceChosenValue = 1;
        position = arrayAdapter2.getPosition("1");
        spinner2.setSelection(position);

    }

    public void cancelHistoryTrip(View v){

        finish();

    }
}