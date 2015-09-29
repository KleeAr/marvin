package ar.com.klee.marvin.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import ar.com.klee.marvin.R;

public class ConfOrientationScreenActivity extends ActionBarActivity {

    private int orientation; //Orientación de la pantalla
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_orientation);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("FIJAR ROTACIÓN DE PANTALLA");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RadioGroup rg = (RadioGroup)findViewById(R.id.radioGroup);
        String radiovalue = ((RadioButton)findViewById(rg.getCheckedRadioButtonId())).getText().toString();

        Toast.makeText(getApplicationContext(), radiovalue , Toast.LENGTH_SHORT).show();

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
