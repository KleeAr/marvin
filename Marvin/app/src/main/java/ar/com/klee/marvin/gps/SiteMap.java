package ar.com.klee.marvin.gps;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.activities.SiteActivity;
import ar.com.klee.marvin.activities.TripActivity;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;

/**
 * A fragment that launches other parts of the demo application.
 */
public class SiteMap extends Fragment {

    private MapView mMapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // inflate and return the layout
        View v = inflater.inflate(R.layout.activity_site, container, false);
        mMapView = (MapView) v.findViewById(R.id.siteMap);
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

    public void setSite(Site site){

        MarkerOptions marker = new MarkerOptions().position(site.getCoordinates());
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)).title(site.getSiteName()).snippet(site.getSiteAddress());
        googleMap.addMarker(marker);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(site.getCoordinates()).zoom(17).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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
                    FileOutputStream out = new FileOutputStream("/sdcard/MARVIN/site.png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    ((SiteActivity)CommandHandlerManager.getInstance().getActivity()).setMapBitmap(snapshot);
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