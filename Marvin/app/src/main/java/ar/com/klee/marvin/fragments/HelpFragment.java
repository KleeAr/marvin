package ar.com.klee.marvin.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;

public class HelpFragment  extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        CommandHandlerManager commandHandlerManager = CommandHandlerManager.getInstance();
        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_HELP,commandHandlerManager.getMainActivity());

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_help, container, false);
        v.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // Set title bar
        ((MainMenuActivity) getActivity()).setActionBarTitle("AYUDA");

        return v;
    }


}
