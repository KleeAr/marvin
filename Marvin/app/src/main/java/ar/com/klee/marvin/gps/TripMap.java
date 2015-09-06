package ar.com.klee.marvin.gps;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.activities.TripActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.handlers.CommandHandler;

/**
 * A fragment that launches other parts of the demo application.
 */
public class TripMap extends Fragment {

    private MapView mMapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // inflate and return the layout
        View v = inflater.inflate(R.layout.activity_trip, container, false);
        mMapView = (MapView) v.findViewById(R.id.tripMap);
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

    public void setTrip(Trip trip){

        MarkerOptions marker = new MarkerOptions().position(trip.getBeginning());
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)).title("Partida").snippet(trip.getBeginningAddress());
        googleMap.addMarker(marker);

        marker = new MarkerOptions().position(trip.getEnding());
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("Llegada").snippet(trip.getEndingAddress());
        googleMap.addMarker(marker);

        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int z = 0; z < trip.getTripPath().size(); z++) {
            Log.d("Step", trip.getTripPath().get(z).getCoordinates().toString());
            LatLng point = trip.getTripPath().get(z).getCoordinates();
            options.add(point);
        }
        googleMap.addPolyline(options);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (TripStep coor : trip.getTripPath()) {
            builder.include(coor.getCoordinates());
        }
        LatLngBounds bounds = builder.build();

        int padding = 3; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.animateCamera(cu);

    }

    public void captureScreen(){

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

                try {
                    FileOutputStream out = new FileOutputStream("/sdcard/MARVIN/trip.png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    ((TripActivity)CommandHandlerManager.getInstance().getActivity()).setMapBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        googleMap.snapshot(callback);
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