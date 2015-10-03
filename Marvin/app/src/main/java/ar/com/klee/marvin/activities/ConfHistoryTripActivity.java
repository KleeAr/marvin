package ar.com.klee.marvin.activities;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        timeMin.add("Ninguno");
        timeMin.add("5");
        timeMin.add("15");
        timeMin.add("30");
        timeMin.add("60");

        final ArrayList<String> tripMin = new ArrayList<String>();
        tripMin.add("Ninguno");
        tripMin.add("1");
        tripMin.add("5");
        tripMin.add("10");
        tripMin.add("15");
        tripMin.add("20");


        Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);



        // Create the ArrayAdapter

        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,timeMin);
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,tripMin);


        // Set the Adapter
        spinner1.setAdapter(arrayAdapter1);
        spinner2.setAdapter(arrayAdapter2);


        // Set the ClickListener for Spinner
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               // Toast.makeText(getApplicationContext(), "Seleccionaste: " + timeMin.get(i), Toast.LENGTH_SHORT).show();
               if(!timeMin.get(i).equals("Ninguno"))
                   UserConfig.setMiniumTripTime(Integer.parseInt(timeMin.get(i)));

            }

            // If no option selected
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }

        });

        // Set the ClickListener for Spinner
        spinner2.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView,View view, int i, long l) {
                //Toast.makeText(getApplicationContext(),"Seleccionaste: "+ tripMin.get(i),Toast.LENGTH_SHORT).show();
                if(!tripMin.get(i).equals("Ninguno"))
                    UserConfig.setMiniumTripDistance(Integer.parseInt(tripMin.get(i)));

            }
            // If no option selected
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
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
}