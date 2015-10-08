package ar.com.klee.marvin.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.configuration.UserConfig;


public class ConfHotSpotActivity extends ActionBarActivity {

    private EditText netName;
    private EditText netPassword;

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
        setContentView(R.layout.activity_configure_hotspot);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         Typeface fBariolBold = Typeface.createFromAsset(getAssets(), "Bariol_Bold.otf");

        titleText = (TextView) findViewById(R.id.activityTitle);
        titleText.setVisibility(TextView.VISIBLE);
        titleText.setTypeface(fBariolBold);
        titleText.setText("Configuración del HotSpot");

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


        netName= (EditText) findViewById(R.id.netName);
        netPassword= (EditText) findViewById(R.id.netPassword);

        netName.setText(UserConfig.getSettings().getHotspotName());
        netPassword.setText(UserConfig.getSettings().getHotspotPassword());

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

    public void saveHotSpot(View v){

        UserConfig.getSettings().setHotspotName(netName.getText().toString());
        UserConfig.getSettings().setHotspotPassword(netPassword.getText().toString());

        finish();

    }

    public void resetHotSpot(View v){

        netName.setText("MRVN");
        netPassword.setText("marvinHotSpot");

    }

    public void cancelHotSpot(View v){

        finish();

    }
}
