package ar.com.klee.marvin.activities;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.gps.Trip;
import ar.com.klee.marvin.gps.TripMap;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;

public class TripActivity extends Activity {

    TripMap fragment;
    Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        addMap();

        trip = TripHistoryActivity.getInstance().getChosenTrip();

    }

    public void addMap(){
        FragmentManager manager = ((MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity()).getManager();
        FragmentTransaction transaction = manager.beginTransaction();
        fragment = new TripMap();
        transaction.add(R.id.tripMap, fragment);
        transaction.commit();
    }

}
