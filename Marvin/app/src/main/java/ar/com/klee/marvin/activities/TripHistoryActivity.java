package ar.com.klee.marvin.activities;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.call.Call;
import ar.com.klee.marvin.call.CallDriver;
import ar.com.klee.marvin.call.HistoryAdapter;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;
import ar.com.klee.marvin.voiceControl.handlers.callHistory.ConsultarRegistroNumeroHandler;

public class TripHistoryActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListView tripListView;
    private List<Call> tripList = new ArrayList<Call>();
    private CommandHandlerManager commandHandlerManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);

        tripListView = (ListView) findViewById(R.id.tripListView);

        //TODO: GET TRIPS

        //tripList.add()

        HistoryAdapter objAdapter = new HistoryAdapter(TripHistoryActivity.this, R.layout.activity_historial_item, tripList);
        tripListView.setAdapter(objAdapter);
        tripListView.setOnItemClickListener(this);

        if(tripList.size()==0)
            Toast.makeText(this, "Historial de Viajes Vacío", Toast.LENGTH_SHORT).show();

        commandHandlerManager = CommandHandlerManager.getInstance();

        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_TRIP_HISTORY,this);
    }

    public void onBackPressed(){
        commandHandlerManager.setNullCommand();
        STTService.getInstance().setIsListening(false);
        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_MAIN, commandHandlerManager.getMainActivity());
        this.finish();
    }

    @Override
    public void onItemClick(AdapterView<?> listview, View v, int position, long id) {

        

    }
}
