package ar.com.klee.marvin.activities;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.fragments.MisViajesFragment;
import ar.com.klee.marvin.gps.Trip;
import ar.com.klee.marvin.gps.TripMap;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;

public class TripActivity extends ActionBarActivity {

    private TripMap fragment;
    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        addMap();

        trip = MisViajesFragment.getInstance().getChosenTrip();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                fragment.setTrip(trip);
            }
        }, 1000);

    }

    public void addMap(){
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragment = new TripMap();
        transaction.add(R.id.tripMap, fragment);
        transaction.commit();
    }

}
