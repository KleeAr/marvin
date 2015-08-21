package ar.com.klee.marvin.gps;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;

/**
 * A fragment that launches other parts of the demo application.
 */
public class MapFragment extends Fragment {

    private double MIN_TRIP_TIME = 0.0;

    MapView mMapView;
    private GoogleMap googleMap;
    private Trip trip;

    private static MapFragment instance;

    private int zoom = 17;

    private double startLatitude = 0.0;
    private double startLongitude = 0.0;
    private String startAddress = "";

    private List<LatLng> tripPath = new ArrayList<LatLng>();
    private String lastAddress = "";

    private double finishLatitude = 0.0;
    private double finishLongitude = 0.0;
    private String finishAddress = "";

    private boolean isSearch = false;

    private Marker lastMarker = null;
    private Marker searchMarker = null;

    public static MapFragment getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Instance not initialized. Call initializeInstance before calling getInstance");
        }
        return instance;
    }

    public static boolean isInstanceInitialized() {
        return instance != null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        instance = this;

        // inflate and return the layout
        View v = inflater.inflate(R.layout.tab_map, container, false);

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();

        // Perform any camera updates here
        return v;
    }

    public void startTrip(double lat, double lon, String address){

        startLatitude = lat;
        startLongitude = lon;
        startAddress = address;

        // create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon));

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)).title("Partida").snippet(address);

        // adding marker
        googleMap.addMarker(marker);

        if(!isSearch) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lon)).zoom(zoom).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        trip = new Trip(lat,lon, address);

    }

    public void refreshMap(double lat, double lon, String address){

        tripPath.add(new LatLng(lat,lon));

        if(lastMarker!=null){
            lastMarker.remove();
        }

        if(searchMarker != null){
            searchMarker.remove();
            searchMarker = null;
        }

        // create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon));

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("Posición Actual").snippet(address);

        // adding marker
        lastMarker = googleMap.addMarker(marker);

        lastAddress = address;

        if(!isSearch) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lon)).zoom(zoom).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public void finishTrip(){

        if(lastMarker!=null){
            lastMarker.remove();
        }

        if(searchMarker != null){
            searchMarker.remove();
            searchMarker = null;
        }

        if(tripPath.size()<1)
            return;

        LatLng coordinates = tripPath.get(tripPath.size()-1);

        trip.setEnding(coordinates);
        trip.setTripPath(tripPath);
        trip.setEndingAddress(lastAddress);

        finishLatitude = coordinates.latitude;
        finishLongitude = coordinates.longitude;

        Geocoder geocoder = new Geocoder(CommandHandlerManager.getInstance().getContext(), Locale.getDefault());
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(finishLatitude, finishLongitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!list.isEmpty()) {
            Address address = list.get(0);
            finishAddress = address.getAddressLine(0);
        }

        // create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(finishLatitude, finishLatitude));

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("Llegada").snippet(finishAddress);

        // adding marker
        googleMap.addMarker(marker);

        trip.setFinishTime(new Date());

        long diffInMs = trip.getFinishTime().getTime() - trip.getStartTime().getTime();

        Long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMs);
        Long hours = minutes/60;
        minutes = minutes - minutes/60;
        trip.setTime(hours.toString() + " hs. " + minutes.toString() + " min.");

        long hourDecimals = (100 * minutes)/60;
        while(hourDecimals > 1){
            hourDecimals = hourDecimals/10;
        }

        long hourWithDecimals = hours + hourDecimals;

        double polylineLength = 0;
        for (int i = 0; i < tripPath.size(); i++) {
            double lat = tripPath.get(i).latitude;
            double lng = tripPath.get(i).longitude;

            float[] results = new float[1];
            if (i > 0)
                Location.distanceBetween(
                    tripPath.get(i-1).latitude,tripPath.get(i-1).longitude,
                    lat,lng,
                    results);

            polylineLength += results [0];
        }

        polylineLength = polylineLength/1000;

        trip.setDistance(String.format("%.2f",polylineLength));

        double velocity = polylineLength/hourWithDecimals;

        trip.setAverageVelocity(String.format("%.2f",velocity));

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int z = 0; z < list.size(); z++) {
            LatLng point = tripPath.get(z);
            options.add(point);
        }
        googleMap.addPolyline(options);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng coor : tripPath) {
            builder.include(coor);
        }
        LatLngBounds bounds = builder.build();

        int padding = 3; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cu);

        captureScreen();

        if(hourWithDecimals > MIN_TRIP_TIME) {

            Log.d("TRIP","Entró");

            //TODO Guardar en Historial. Consultar si supera el tiempo mínimo de viaje

            MainMenuActivity mma = (MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity();

            SharedPreferences mPrefs = mma.getPreferences(mma.MODE_PRIVATE);

            Integer numberOfTrips = mPrefs.getInt("NumberOfTrips",0);

            numberOfTrips++;

            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(trip);
            prefsEditor.putInt("NumberOfTrips",numberOfTrips);
            prefsEditor.putString("Trip"+numberOfTrips.toString(), json);
            prefsEditor.commit();

        }

    }

    public void captureScreen()
    {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback(){

            Bitmap bitmap;

            public void onSnapshotReady(Bitmap snapshot) {

                bitmap = snapshot;

                File mediaStorageDir = new File("/sdcard/", "MARVIN");

                //if this "JCGCamera folder does not exist
                if (!mediaStorageDir.exists()) {
                    //if you cannot make this folder return
                    if (!mediaStorageDir.mkdirs()) {
                        return;
                    }
                }

                String timeStamp = new SimpleDateFormat("yyMMdd_HHmmss").format(new Date());

                try {
                    FileOutputStream out = new FileOutputStream("/sdcard/MARVIN" + finishAddress + "_" + timeStamp + ".png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        googleMap.snapshot(callback);
    }

    public LatLng getCoordinates(String address){

        Geocoder geocoder = new Geocoder(CommandHandlerManager.getInstance().getActivity());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(address, 1, -55.0, -73.0, -21.0, -56.0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Double latitude = 0.0;
        Double longitude = 0.0;

        if(addresses!=null && addresses.size() > 0) {
            latitude= addresses.get(0).getLatitude();
            longitude= addresses.get(0).getLongitude();
        }

        return new LatLng(latitude,longitude);

    }

    public void goToCurrentPosition(){

        CameraPosition cameraPosition = new CameraPosition.Builder().target(tripPath.get(tripPath.size()-1)).zoom(zoom).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    public void search(String address, LatLng coordinates){

        if(searchMarker != null){
            searchMarker.remove();
        }

        // create marker
        MarkerOptions marker = new MarkerOptions().position(coordinates);

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("Posición Actual").snippet(address);

        // adding marker
        searchMarker = googleMap.addMarker(marker);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(coordinates).zoom(zoom).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    public void navigate(double lat, double lon){

        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" +
                "saddr=" + tripPath.get(tripPath.size()-1).latitude + "," + tripPath.get(tripPath.size()-1).longitude +
                "&daddr=" + lat + "," + lon));
        intent.setClassName("com.google.android.apps.maps","com.google.andrid.maps.MapsActivity");
        startActivity(intent);

    }

    public void setSearch(boolean type){
        isSearch = type;
    }

    public boolean getSearch() {return isSearch;}

    public void setZoom(int value){
        zoom = value;
        CameraPosition cameraPosition = new CameraPosition.Builder().zoom(zoom).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public int getZoom(){
        return zoom;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}