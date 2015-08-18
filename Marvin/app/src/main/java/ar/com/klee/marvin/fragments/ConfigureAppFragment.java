package ar.com.klee.marvin.fragments;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ar.com.klee.marvin.R;

public class ConfigureAppFragment extends Fragment {


    public ConfigureAppFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //  View v = inflater.inflate(R.layout.fragment_mis_sitios, container, false);

        View v = inflater.inflate(R.layout.fragment_congiure_app, container, false);
        return v;
    }
}