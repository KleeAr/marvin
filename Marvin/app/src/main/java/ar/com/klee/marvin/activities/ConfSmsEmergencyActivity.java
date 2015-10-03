package ar.com.klee.marvin.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.configuration.UserConfig;


public class ConfSmsEmergencyActivity extends ActionBarActivity {

    private EditText toSMS;
    private EditText bodySMS;

    private Switch switch1;
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
        setContentView(R.layout.activity_configure_sms_emergency);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Typeface fBariolBold = Typeface.createFromAsset(getAssets(), "Bariol_Bold.otf");

        titleText = (TextView) findViewById(R.id.activityTitle);
        titleText.setVisibility(TextView.VISIBLE);
        titleText.setTypeface(fBariolBold);
        titleText.setText("SMS de emergencia");

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


        toSMS = (EditText) findViewById(R.id.toSMS);
        bodySMS = (EditText) findViewById(R.id.bodySMS);


        switch1 = (Switch) findViewById(R.id.switch1);
        // set the switch to OFF
        switch1.setChecked(false);
        // attach a listener to check for changes in state
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "Activado", Toast.LENGTH_SHORT).show();
                    UserConfig.setEmergencyEnable(true);

                } else {
                    Toast.makeText(getApplicationContext(), "Desactivado", Toast.LENGTH_SHORT).show();
                    UserConfig.setEmergencyEnable(false);
                }

            }
        });




    }
    @Override
    public void onBackPressed() {
        UserConfig.setEmergencyNumber(toSMS .getText().toString());
        UserConfig.setEmergencySMS(bodySMS.getText().toString());
        this.finish();
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
