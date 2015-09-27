package ar.com.klee.marvin.activities;



import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;


import java.util.ArrayList;

import ar.com.klee.marvin.R;

public class ConfSpeedAlertActivity extends ActionBarActivity {

    private Switch switch1;
    private Switch switch2;

    /*public AlertasVelocidadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_configure_speed_alert, container, false);
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_speed_alert);

        getSupportActionBar().setTitle("ALERTAS DE VELOCIDAD");

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

       // spinner1.setSelection(0);


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
}