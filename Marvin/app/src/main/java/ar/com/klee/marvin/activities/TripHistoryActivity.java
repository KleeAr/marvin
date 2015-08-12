package ar.com.klee.marvin.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.call.Call;
import ar.com.klee.marvin.call.HistoryAdapter;
import ar.com.klee.marvin.gps.Trip;
import ar.com.klee.marvin.gps.TripAdapter;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;

public class TripHistoryActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListView tripListView;
    private List<Trip> tripList = new ArrayList<Trip>();
    private CommandHandlerManager commandHandlerManager;

    private static TripHistoryActivity instance;
    private Trip chosenTrip;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);

        instance = this;

        tripListView = (ListView) findViewById(R.id.tripListView);

        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);

        int numberOfTrips = mPrefs.getInt("NumberOfTrips",0);

        for(Integer i=1; i<=numberOfTrips; i++) {
            Gson gson = new Gson();
            String json = mPrefs.getString("Trip"+i.toString(), "");
            tripList.add(gson.fromJson(json, Trip.class));
        }

        TripAdapter objAdapter = new TripAdapter(TripHistoryActivity.this, R.layout.item_trip_history, tripList);
        tripListView.setAdapter(objAdapter);
        tripListView.setOnItemClickListener(this);

        if(tripList.size()==0)
            Toast.makeText(this, "Historial de Viajes Vacío", Toast.LENGTH_SHORT).show();

        commandHandlerManager = CommandHandlerManager.getInstance();

        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_TRIP_HISTORY,this);
    }

    public static TripHistoryActivity getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Instance not initialized. Call initializeInstance before calling getInstance");
        }
        return instance;
    }

    public void onBackPressed(){
        commandHandlerManager.setNullCommand();
        STTService.getInstance().setIsListening(false);
        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_MAIN, commandHandlerManager.getMainActivity());
        this.finish();
    }

    @Override
    public void onItemClick(AdapterView<?> listview, View v, int position, long id) {

        chosenTrip = tripList.get(position);

    }

    public Trip getChosenTrip(){
        return chosenTrip;
    }
}
