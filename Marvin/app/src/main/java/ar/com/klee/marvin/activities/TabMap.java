package ar.com.klee.marvin.activities;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;


public class TabMap extends Fragment implements View.OnLongClickListener {

    private static TabMap instance;
    private View v = null;

    public static TabMap getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Instance not initialized. Call initializeInstance before calling getInstance");
        }
        return instance;
    }

    public static boolean isInstanceInitialized() {
        return instance != null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        instance = this;

        if(!MainMenuActivity.mapFragment.isAdded()) {

            MainMenuActivity.isMapCreated = true;

            v = inflater.inflate(R.layout.tab_map, container, false);

            FragmentManager manager = ((MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity()).getManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.mapView, MainMenuActivity.mapFragment);
            transaction.commit();

        }
        return v;

    }

    @Override
    public boolean onLongClick(View v) {

        Intent intent = new Intent(CommandHandlerManager.getInstance().getContext(), MapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        CommandHandlerManager.getInstance().getContext().startActivity(intent);

        return false;
    }
}