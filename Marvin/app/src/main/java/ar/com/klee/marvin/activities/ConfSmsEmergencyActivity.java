package ar.com.klee.marvin.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import ar.com.klee.marvin.R;


public class ConfSmsEmergencyActivity extends ActionBarActivity {

    private String emergencyNumber; //Número al que enviarle un sms de emergencia
    private String emergencySMS; //Sms de emergencia a enviar

    private EditText toSMS;
    private EditText bodySMS;

    private Switch switch1;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_sms_emergency);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("SMS DE EMERGENCIA");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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


                } else {

                    Toast.makeText(getApplicationContext(), "Desactivado", Toast.LENGTH_SHORT).show();
                }

            }
        });




    }
    @Override
    public void onBackPressed() {
        emergencyNumber=toSMS .getText().toString();
        emergencySMS=bodySMS.getText().toString();
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
