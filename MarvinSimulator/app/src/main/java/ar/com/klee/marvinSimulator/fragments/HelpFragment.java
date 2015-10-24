package ar.com.klee.marvinSimulator.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ar.com.klee.marvinSimulator.activities.MainMenuActivity;
import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;

public class HelpFragment  extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        CommandHandlerManager commandHandlerManager = CommandHandlerManager.getInstance();
        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_HELP,commandHandlerManager.getMainActivity());

        // Inflate the layout for this fragment
        View v = inflater.inflate(ar.com.klee.marvinSimulator.R.layout.fragment_help, container, false);

        // Set title bar
        ((MainMenuActivity) getActivity()).setActionBarTitle("AYUDA");

        return v;
    }


}
