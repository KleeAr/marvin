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

public class ConfSpeedAlertActivity extends ActionBarActivity {

    private Switch switch1;
    private Switch switch2;
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
        setContentView(R.layout.activity_configure_speed_alert);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       Typeface fBariolBold = Typeface.createFromAsset(getAssets(), "Bariol_Bold.otf");

        titleText = (TextView) findViewById(R.id.activityTitle);
        titleText.setVisibility(TextView.VISIBLE);
        titleText.setTypeface(fBariolBold);
        titleText.setText("Alertas de Velocidad");

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

       // final EditText etSpeed1 = (EditText) findViewById(R.id.et_vel1);

        final ArrayList<String> speedVelocity = new ArrayList<String>();
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



       Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);



        // Create the ArrayAdapter

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,speedVelocity);


        // Set the Adapter
        spinner1.setAdapter(arrayAdapter);
        spinner2.setAdapter(arrayAdapter);


        // Set the ClickListener for Spinner
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), "Seleccionaste: " + speedVelocity.get(i), Toast.LENGTH_SHORT).show();

            }

            // If no option selected
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }

        });

        // Set the ClickListener for Spinner
        spinner2.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView,View view, int i, long l) {
                Toast.makeText(getApplicationContext(),"Seleccionaste: "+ speedVelocity.get(i),Toast.LENGTH_SHORT).show();

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

                    Toast.makeText(getApplicationContext(), "Activado", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(getApplicationContext(), "NO", Toast.LENGTH_SHORT).show();
                }

            }
        });


        switch2 = (Switch) findViewById(R.id.switch2);

// set the switch to OFF
        switch2.setChecked(false);
// attach a listener to check for changes in state
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    Toast.makeText(getApplicationContext(), "Activado", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(getApplicationContext(), "NO", Toast.LENGTH_SHORT).show();
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
}