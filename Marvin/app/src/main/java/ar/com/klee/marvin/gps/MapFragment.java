package ar.com.klee.marvin.gps;

import android.content.Intent;
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

import ar.com.klee.marvin.R;

/**
 * A fragment that launches other parts of the demo application.
 */
public class MapFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;

    private static MapFragment instance;

    private double startLatitude = 0.0;
    private double startLongitude = 0.0;
    private String startAddress = "";

    private double finishLatitude = 0.0;
    private double finishLongitude = 0.0;
    private String finishAddress = "";

    Marker lastMarker = null;

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

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lon)).zoom(17).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    public void refreshMap(double lat, double lon, String address){

        if(lastMarker!=null){
            lastMarker.remove();
        }

        // create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon));

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title("Posición Actual").snippet(address);

        // adding marker
        lastMarker = googleMap.addMarker(marker);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lon)).zoom(17).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void finishTrip(double lat, double lon, String address){

        if(lastMarker!=null){
            lastMarker.remove();
        }

        finishLatitude = lat;
        finishLongitude = lon;
        finishAddress = address;

        // create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lon));

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("Llegada").snippet(address);

        // adding marker
        googleMap.addMarker(marker);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lon)).zoom(16).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    public void navigate(double lat, double lon){

        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" + "saddr=" + -34.683607 + "," + -58.511295 + "&daddr=" + lat + "," + lon));
        intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
        startActivity(intent);

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