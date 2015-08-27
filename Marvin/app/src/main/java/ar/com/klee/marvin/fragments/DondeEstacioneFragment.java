package ar.com.klee.marvin.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.gps.ParkingMap;
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
        final LatLng coordinates = gson.fromJson(json, LatLng.class);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                fragment.setParkSite(coordinates.latitude,coordinates.longitude);
            }
        }, 1000);


        return v;
    }



}
