package ar.com.klee.marvin.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.activities.MainMenuActivity;

public class HelpFragment  extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_help, container, false);

        // Set title bar
        ((MainMenuActivity) getActivity()).setActionBarTitle("AYUDA");

        return v;
    }


}
