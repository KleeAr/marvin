package ar.com.klee.marvinSimulator.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


public class ConfTermsActivity extends ActionBarActivity {

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
        setContentView(ar.com.klee.marvinSimulator.R.layout.activity_terms);

        toolbar = (Toolbar) findViewById(ar.com.klee.marvinSimulator.R.id.tool_bar);
        setSupportActionBar(toolbar);
        // Set title bar

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Typeface fBariolBold = Typeface.createFromAsset(getAssets(), "Bariol_Bold.otf");

        titleText = (TextView) findViewById(ar.com.klee.marvinSimulator.R.id.activityTitle);
        titleText.setVisibility(TextView.VISIBLE);
        titleText.setTypeface(fBariolBold);
        titleText.setText("Términos y Condiciones");

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


      /*  titleText = (TextView) findViewById(R.id.activityTitle);
        titleText.setVisibility(TextView.VISIBLE);
        titleText.setTypeface(fBariolBold);
        titleText.setText("TERMINOS Y CONDICIONES");*/


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