package ar.com.klee.marvin.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.hudomju.swipe.OnItemClickListener;
import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.SwipeableItemClickListener;
import com.hudomju.swipe.adapter.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import ar.com.klee.marvin.CardAdapter;
import ar.com.klee.marvin.R;
import ar.com.klee.marvin.Site;
import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.gps.ParkingMap;
import ar.com.klee.marvin.gps.Trip;
import ar.com.klee.marvin.gps.TripMap;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;


public class DondeEstacioneFragment extends Fragment {

    private ParkingMap fragment;

    private CommandHandlerManager commandHandlerManager;

    public DondeEstacioneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        commandHandlerManager = CommandHandlerManager.getInstance();

        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_PARKING,commandHandlerManager.getMainActivity());

        View v = inflater.inflate(R.layout.fragment_donde_estacione,container, false);

        FragmentManager manager = ((MainMenuActivity)commandHandlerManager.getMainActivity()).getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragment = new ParkingMap();
        transaction.add(R.id.parkingMap, fragment);
        transaction.commit();

        MainMenuActivity mainMenuActivity = (MainMenuActivity)commandHandlerManager.getMainActivity();

        SharedPreferences mPrefs = mainMenuActivity.getPreferences(mainMenuActivity.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = mPrefs.getString("ParkingSite", "");
        LatLng coordinates = gson.fromJson(json, LatLng.class);

        fragment.setParkSite(coordinates.latitude,coordinates.longitude);

        return v;
    }



}
