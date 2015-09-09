package ar.com.klee.marvin.fragments;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

import ar.com.klee.marvin.R;

public class ComandosDeVozFragment extends Fragment {

    private Switch mySwitch;

    public ComandosDeVozFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_configure_app, container, false);

        final NumberPicker np = (NumberPicker) v.findViewById(R.id.numberPicker);
        np.setMinValue(40);
        np.setMaxValue(110);


        mySwitch = (Switch) v.findViewById(R.id.switch1);

// set the switch to OFF
        mySwitch.setChecked(false);
// attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

                if (isChecked) {

                    Toast.makeText(getActivity(), np.getValue() + "", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(getActivity(),"NO", Toast.LENGTH_SHORT).show();
                }

            }
        });





        final ArrayList<String> difficultyLevelOptionsList = new ArrayList<String>();

        Spinner spinner = (Spinner) v.findViewById(R.id.spinner);

        difficultyLevelOptionsList.add("Repetir cada 5 segundos");
        difficultyLevelOptionsList.add("Repetir cada 15 segundos");
        difficultyLevelOptionsList.add("Repetir cada 30 segundos");

        // Create the ArrayAdapter

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item,difficultyLevelOptionsList);

        // Set the Adapter
        spinner.setAdapter(arrayAdapter);


        // Set the ClickListener for Spinner
        spinner.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> adapterView,View view, int i, long l) {
                Toast.makeText(getActivity(),"Seleccionaste: "+ difficultyLevelOptionsList.get(i),Toast.LENGTH_SHORT).show();

            }
            // If no option selected
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }

        });

        return v;
    }
}