package ar.com.klee.marvin.activities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.configuration.UserConfig;
import ar.com.klee.marvin.gps.BiggerMapFragment;
import ar.com.klee.marvin.gps.MapFragment;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;

public class MapActivity extends ActionBarActivity {

    private BiggerMapFragment biggerMapFragment;
    private CommandHandlerManager commandHandlerManager;
    private EditText et_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(UserConfig.getSettings().getOrientation() == UserConfig.ORIENTATION_PORTRAIT)
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);

        setContentView(R.layout.activity_map);

        if(MapFragment.isInstanceInitialized())
            MapFragment.getInstance().setSearch(true);
        else
            new TabMap();

        if(BiggerMapFragment.isInstanceInitialized())
            biggerMapFragment = BiggerMapFragment.getInstance();
        else
            biggerMapFragment = new BiggerMapFragment();

        commandHandlerManager = CommandHandlerManager.getInstance();

        if(!biggerMapFragment.isAdded()) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.biggerMap, biggerMapFragment);
            transaction.commit();
        }

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

        if(MapFragment.isInstanceInitialized())
            MapFragment.getInstance().setSearch(false);

        commandHandlerManager.setNullCommand();
        STTService.getInstance().setIsListening(false);
        commandHandlerManager.defineActivity(CommandHandlerManager.ACTIVITY_MAIN, commandHandlerManager.getMainActivity());
        this.finish();
    }

    public int zoomIn(){

        int zoom = biggerMapFragment.getZoom() + 1;

        if(zoom == 21)
            zoom = 20;

        biggerMapFragment.setZoom(zoom);

        return zoom;

    }

    public void zoomIn(View v){

        int zoom = biggerMapFragment.getZoom() + 1;

        if(zoom == 21)
            zoom = 20;

        biggerMapFragment.setZoom(zoom);

    }

    public int zoomOut(){

        int zoom = biggerMapFragment.getZoom() - 1;

        if(zoom == 0)
            zoom = 1;

        biggerMapFragment.setZoom(zoom);

        return zoom;

    }

    public void zoomOut(View v){

        int zoom = biggerMapFragment.getZoom() - 1;

        if(zoom == 0)
            zoom = 1;

        biggerMapFragment.setZoom(zoom);

    }

    public int setZoom(int zoom){
        biggerMapFragment.setZoom(zoom);
        return zoom;
    }

    public void currentLocation(){

        biggerMapFragment.goToCurrentPosition();

    }

    public void currentLocation(View v){

        biggerMapFragment.goToCurrentPosition();

    }

    public void search(String address){

        et_search.setText(address);

        LatLng coordinates = biggerMapFragment.getCoordinates(address);

        biggerMapFragment.search(address,coordinates);

    }

    public void search(View v){

        hideSoftKeyboard(MapActivity.this, v);

        String address = et_search.getText().toString();

        if(address == ""){
            Toast.makeText(this, "Ingrese una dirección", Toast.LENGTH_SHORT).show();
            return;
        }

        LatLng coordinates = biggerMapFragment.getCoordinates(address);

        biggerMapFragment.search(address,coordinates);

    }

    public void navigate(String address){

        et_search.setText(address);

        LatLng coordinates = biggerMapFragment.getCoordinates(address);

        biggerMapFragment.navigate(coordinates.latitude, coordinates.longitude);

    }

    public void navigate(View v){

        hideSoftKeyboard(MapActivity.this, v);

        String address = et_search.getText().toString();

        if(address == ""){
            Toast.makeText(this, "Ingrese una dirección", Toast.LENGTH_SHORT).show();
        }

        LatLng coordinates = biggerMapFragment.getCoordinates(address);

        biggerMapFragment.navigate(coordinates.latitude, coordinates.longitude);

    }

    public static void hideSoftKeyboard (Activity activity, View view)    {
        InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
}
