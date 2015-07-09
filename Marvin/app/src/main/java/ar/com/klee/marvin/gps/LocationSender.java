package ar.com.klee.marvin.gps;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.cmc.music.metadata.IMusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Stack;

public class LocationSender {

    private String address;
    private String town;

    private double actualLatitude = 0.0;
    private double actualLongitude = 0.0;
    private double previousLatitude = 0.0;
    private double previousLongitude = 0.0;


    private Context context;

    public LocationSender(Context context){
        /* Use the LocationManager class to obtain GPS locations */

        this.context = context;

        LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
                // debido a la deteccion de un cambio de ubicacion

                double longitude = location.getLongitude();
                double latitude = location.getLatitude();

                previousLatitude = actualLatitude;
                previousLongitude = actualLongitude;

                actualLatitude = latitude;
                actualLongitude = longitude;

                setLocation(latitude, longitude);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                /*
                   Este metodo se ejecuta cada vez que se detecta un cambio en el
                   status del proveedor de localizacion (GPS)
                   Los diferentes Status son:
                   OUT_OF_SERVICE -> Si el proveedor esta fuera de servicio
                   TEMPORARILY_UNAVAILABLE -> Temporalmente no disponible pero se
                   espera que este disponible en breve
                   AVAILABLE -> Disponible
                */
            }

            @Override
            public void onProviderEnabled(String provider) {
                // Este metodo se ejecuta cuando el GPS es activado

            }

            @Override
            public void onProviderDisabled(String provider) {
                // Este metodo se ejecuta cuando el GPS es desactivado

            }
        };

        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, locationListener);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

    }

    public void setLocation(double latitude, double longitude) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (latitude != 0.0 && longitude != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(latitude, longitude, 1);
                if (!list.isEmpty()) {
                    Address address = list.get(0);
                    this.address = address.getAddressLine(0);
                    this.town = address.getAddressLine(1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String nextStreet(){

        if(previousLongitude == 0.0 || previousLongitude == 0.0)
            return "error";

        IntersectionSender intersection = new IntersectionSender(actualLatitude, actualLongitude, previousLatitude, previousLongitude, context);

        return intersection.findStreet(IntersectionSender.NEXT_STREET);

    }

    public String previousStreet(){

        if(previousLongitude == 0.0 || previousLongitude == 0.0)
            return "error";

        IntersectionSender intersection = new IntersectionSender(actualLatitude, actualLongitude, previousLatitude, previousLongitude, context);

        return intersection.findStreet(IntersectionSender.PREVIOUS_STREET);

    }

    public String getAddress(){
        return address;
    }

    public String getTown(){
        return town;
    }
}
