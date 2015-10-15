package ar.com.klee.marvinSimulator.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import ar.com.klee.marvinSimulator.configuration.UserConfig;


public class ConfSmsEmergencyActivity extends ActionBarActivity {

    private EditText toSMS;
    private EditText bodySMS;

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
        setContentView(ar.com.klee.marvinSimulator.R.layout.activity_configure_sms_emergency);

        toolbar = (Toolbar) findViewById(ar.com.klee.marvinSimulator.R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Typeface fBariolBold = Typeface.createFromAsset(getAssets(), "Bariol_Bold.otf");

        titleText = (TextView) findViewById(ar.com.klee.marvinSimulator.R.id.activityTitle);
        titleText.setVisibility(TextView.VISIBLE);
        titleText.setTypeface(fBariolBold);
        titleText.setText("SMS de emergencia");

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


        toSMS = (EditText) findViewById(ar.com.klee.marvinSimulator.R.id.toSMS);
        bodySMS = (EditText) findViewById(ar.com.klee.marvinSimulator.R.id.bodySMS);

        toSMS.setText(UserConfig.getSettings().getEmergencyNumber());
        bodySMS.setText(UserConfig.getSettings().getEmergencySMS());

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

    public void saveSmsEmergency(View v){

        UserConfig.getSettings().setEmergencyNumber(toSMS.getText().toString());
        UserConfig.getSettings().setEmergencySMS(bodySMS.getText().toString());

        finish();

    }

    public void resetSmsEmergency(View v){

        toSMS.setText("");
        bodySMS.setText("");

    }

    public void cancelSmsEmergency(View v){

        finish();

    }
}
