package ar.com.klee.marvin.gps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.activities.TabMap;

public class LocationSender {

    private String address;
    private String town;
    private String state;
    private String speed;

    private double actualLatitude = 0.0;
    private double actualLongitude = 0.0;
    private double previousLatitude = 0.0;
    private double previousLongitude = 0.0;

    private MapFragment mapFragment;
    private BiggerMapFragment biggerMapFragment;
    private boolean isFirstLocation = true;
    private boolean isFirstLocation2 = true;

    private Context context;

    public LocationSender(Context context, MapFragment mf){
        /* Use the LocationManager class to obtain GPS locations */

        this.context = context;

        mapFragment = mf;

        LocationManager mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
                // debido a la deteccion de un cambio de ubicacion

                double longitude = location.getLongitude();
                double latitude = location.getLatitude();

                double velocity = location.getSpeed();

                previousLatitude = actualLatitude;
                previousLongitude = actualLongitude;

                actualLatitude = latitude;
                actualLongitude = longitude;

                setLocation(latitude, longitude);
                setSpeed(velocity);
                updateScreen();

                if(MainMenuActivity.isMapCreated) {
                    if (!isFirstLocation)
                        mapFragment.refreshMap(actualLatitude, actualLongitude, address);
                    else {
                        mapFragment.startTrip(actualLatitude, actualLongitude, address);
                        isFirstLocation = false;
                    }
                }else{
                    new TabMap();
                }

                if(BiggerMapFragment.isInstanceInitialized()) {
                    if (!isFirstLocation2)
                        biggerMapFragment.refreshMap(actualLatitude, actualLongitude, address);
                    else {
                        biggerMapFragment = BiggerMapFragment.getInstance();
                        biggerMapFragment.startTrip(actualLatitude, actualLongitude, address);
                        isFirstLocation2 = false;
                    }
                }else{
                    biggerMapFragment = new BiggerMapFragment();
                }

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

        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

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
                    this.town = address.getLocality();
                    this.state = address.getAddressLine(2);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateScreen(){
        MainMenuActivity.cityText.setText(getTown()+", "+getState());
        MainMenuActivity.mainStreet.setText(getAddress());
        MainMenuActivity.speed.setText(getSpeed());

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

    public  void setSpeed(double speed){
        this.speed = String.valueOf((int) speed);
    }

    public String getSpeed(){
        return speed;
    }

    public String getState(){
        return state;
    }

    public String getAddress(){
        return address;
    }

    public String getTown(){
        return town;
    }
}
