package ar.com.klee.marvinSimulator.gps;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import ar.com.klee.marvinSimulator.voiceControl.CommandHandlerManager;

/**
 * A fragment that launches other parts of the demo application.
 */
public class BiggerMapFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;

    private static BiggerMapFragment instance;

    private int zoom = 17;

    private double startLatitude;
    private double startLongitude;
    private String startAddress;

    private double lastLatitude;
    private double lastLongitude;

    private Marker lastMarker = null;
    private Marker searchMarker = null;

    public static BiggerMapFragment getInstance() {
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
        View v = inflater.inflate(ar.com.klee.marvinSimulator.R.layout.activity_map, container, false);

        mMapView = (MapView) v.findViewById(ar.com.klee.marvinSimulator.R.id.biggerMap);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();

        if(LocationSender.getInstance().getStartLatitude() != 0.0 && LocationSender.getInstance().getStartLongitude() != 0.0) {

            MarkerOptions marker = new MarkerOptions().position(new LatLng(LocationSender.getInstance().getStartLatitude(), LocationSender.getInstance().getStartLongitude()));

            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)).title("Partida").snippet(LocationSender.getInstance().getStartAddress());

            // adding marker
            googleMap.addMarker(marker);

        }

        MarkerOptions marker2 = new MarkerOptions().position(new LatLng(LocationSender.getInstance().getActualLatitude(), LocationSender.getInstance().getActualLongitude()));

        // Changing marker icon
        marker2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("Posición Actual").snippet(LocationSender.getInstance().getActualAddress());

        // adding marker
        lastMarker = googleMap.addMarker(marker2);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(LocationSender.getInstance().getActualLatitude(),LocationSender.getInstance().getActualLongitude())).zoom(zoom).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        // Perform any camera updates here
        return v;
    }

    public void refreshMap(double lat, double lon, String address){

        if(lastMarker!=null){
            lastMarker.remove();
        }

        lastLatitude = lat;
        lastLongitude = lon;

        // create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon));

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("Posición Actual").snippet(address);

        // adding marker
        lastMarker = googleMap.addMarker(marker);

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

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lastLatitude,lastLongitude)).zoom(zoom).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    public void search(String address, LatLng coordinates){

        if(searchMarker != null){
            searchMarker.remove();
        }

        // create marker
        MarkerOptions marker = new MarkerOptions().position(coordinates);

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title("Búsqueda").snippet(address);

        // adding marker
        searchMarker = googleMap.addMarker(marker);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(coordinates).zoom(zoom).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    public void navigate(double lat, double lon){

        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" +
                "saddr=" + lastLatitude + "," + lastLongitude +
                "&daddr=" + lat + "," + lon));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
        startActivity(intent);

    }

    public void setZoom(int value){
        zoom = value;
        CameraPosition cameraPosition = new CameraPosition.Builder().target(googleMap.getCameraPosition().target).zoom(zoom).build();
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