package ar.com.klee.marvinSimulator.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import ar.com.klee.marvinSimulator.configuration.UserConfig;

public class ConfOrientationScreenActivity extends ActionBarActivity {

    private Toolbar toolbar;
    public TextView titleText;
    public TextView cityText;
    public ImageView weatherIconImageView;
    public TextView temperatureTextView;
    public TextView weekDay;
    public TextView dateText;

    private RadioButton buttonPortrait;
    private RadioButton buttonLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ar.com.klee.marvinSimulator.R.layout.activity_configure_orientation);

        toolbar = (Toolbar) findViewById(ar.com.klee.marvinSimulator.R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Typeface fBariolBold = Typeface.createFromAsset(getAssets(), "Bariol_Bold.otf");

        titleText = (TextView) findViewById(ar.com.klee.marvinSimulator.R.id.activityTitle);
        titleText.setVisibility(TextView.VISIBLE);
        titleText.setTypeface(fBariolBold);
        titleText.setText("Fijar Rotación de Pantalla");

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

        buttonPortrait = (RadioButton)findViewById(ar.com.klee.marvinSimulator.R.id.radioButton1);
        buttonLandscape = (RadioButton)findViewById(ar.com.klee.marvinSimulator.R.id.radioButton2);

        if(UserConfig.getSettings().getOrientation() == UserConfig.ORIENTATION_PORTRAIT){
            buttonPortrait.setChecked(true);
            buttonLandscape.setChecked(false);
        }else{
            buttonPortrait.setChecked(false);
            buttonLandscape.setChecked(true);
        }

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

    public void saveOrientationScreen(View v){

        if(buttonPortrait.isChecked())
            UserConfig.getSettings().setOrientation(UserConfig.ORIENTATION_PORTRAIT);
        else
            UserConfig.getSettings().setOrientation(UserConfig.ORIENTATION_LANDSCAPE);

        finish();

    }

    public void resetOrientationScreen(View v){

        buttonPortrait.setChecked(true);
        buttonLandscape.setChecked(false);

    }

    public void cancelOrientationScreen(View v){

        finish();

    }
}
