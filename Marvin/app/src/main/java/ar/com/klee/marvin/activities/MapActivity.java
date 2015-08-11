package ar.com.klee.marvin.activities;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.gps.MapFragment;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;

public class MapActivity extends ActionBarActivity {

    private MapFragment mapFragment;
    private CommandHandlerManager commandHandlerManager;
    private EditText et_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapFragment = MapFragment.getInstance();
        commandHandlerManager = CommandHandlerManager.getInstance();

        if(!mapFragment.isAdded()) {
            FragmentManager manager = ((MainMenuActivity) commandHandlerManager.getMainActivity()).getManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.mapView, mapFragment);
            transaction.commit();
        }

        mapFragment.setSearch(true);

        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_MAP,this);

        et_search = (EditText) findViewById(R.id.et_search);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){
        commandHandlerManager.setNullCommand();
        STTService.getInstance().setIsListening(false);
        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_MAIN, commandHandlerManager.getMainActivity());
        this.finish();
    }

    public int zoomIn(){

        int zoom = mapFragment.getZoom() + 1;

        if(zoom == 21)
            zoom = 20;

        mapFragment.setZoom(zoom);

        return zoom;

    }

    public void zoomIn(View v){

        int zoom = mapFragment.getZoom() + 1;

        if(zoom == 21)
            zoom = 20;

        mapFragment.setZoom(zoom);

    }

    public int zoomOut(){

        int zoom = mapFragment.getZoom() - 1;

        if(zoom == 0)
            zoom = 1;

        mapFragment.setZoom(zoom);

        return zoom;

    }

    public void zoomOut(View v){

        int zoom = mapFragment.getZoom() - 1;

        if(zoom == 0)
            zoom = 1;

        mapFragment.setZoom(zoom);

    }

    public int setZoom(int zoom){
        mapFragment.setZoom(zoom);
        return zoom;
    }

    public void currentLocation(){

        mapFragment.goToCurrentPosition();

    }

    public void currentLocation(View v){

        mapFragment.goToCurrentPosition();

    }

    public void search(String address){

        et_search.setText(address);

        LatLng coordinates = mapFragment.getCoordinates(address);

        mapFragment.search(address,coordinates);

    }

    public void search(View v){

        String address = et_search.getText().toString();

        if(address == ""){
            Toast.makeText(this, "Ingrese una dirección", Toast.LENGTH_SHORT).show();
        }

        LatLng coordinates = mapFragment.getCoordinates(address);

        mapFragment.search(address,coordinates);

    }

    public void navigate(String address){

        et_search.setText(address);

        LatLng coordinates = mapFragment.getCoordinates(address);

        mapFragment.navigate(coordinates.latitude, coordinates.longitude);

    }

    public void navigate(View v){

        String address = et_search.getText().toString();

        if(address == ""){
            Toast.makeText(this, "Ingrese una dirección", Toast.LENGTH_SHORT).show();
        }

        LatLng coordinates = mapFragment.getCoordinates(address);

        mapFragment.navigate(coordinates.latitude, coordinates.longitude);

    }
}
