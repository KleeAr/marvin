package ar.com.klee.marvin.gps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.activities.TabMap;
import ar.com.klee.marvin.fragments.MainMenuFragment;

public class LocationSender {

    private String address = "Buscando calle...";
    private String town = "Buscando ciudad...";
    private String state = "";
    private String speed = "NA";

    private double actualLatitude = 0.0;
    private double actualLongitude = 0.0;
    private Date actualTime;
    private double previousLatitude = 0.0;
    private double previousLongitude = 0.0;
    private Date previousTime;

    private double velocity = 0.0;

    private MapFragment mapFragment;
    private BiggerMapFragment biggerMapFragment;
    private boolean isFirstLocation = true;
    private boolean isFirstLocation2 = true;

    private LocationManager mlocManager;
    private LocationListener locationListener;

    private Context context;

    public LocationSender(Context context, MapFragment mf){
        /* Use the LocationManager class to obtain GPS locations */

        this.context = context;

        mapFragment = mf;

        mlocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
                // debido a la deteccion de un cambio de ubicacion

                double longitude = location.getLongitude();
                double latitude = location.getLatitude();

                //Log.d("GPS","Recibo nuevas coordenadas. Latitud = " + ((Double)latitude).toString() + "; Longitud = " + ((Double)longitude).toString());

                previousLatitude = actualLatitude;
                previousLongitude = actualLongitude;
                previousTime = actualTime;

                actualLatitude = latitude;
                actualLongitude = longitude;
                actualTime = new Date();

                if(previousLatitude!=0.0) {
                    long diffInMs = actualTime.getTime() - previousTime.getTime();
                    double seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
                    double hours = seconds / 3600;

                    float[] results = new float[1];
                    Location.distanceBetween(previousLatitude, previousLongitude,
                            actualLatitude, actualLongitude,
                            results);
                    double polylineLength = results[0];

                    if(polylineLength > 100)
                        return;

                    polylineLength = polylineLength / 1000;

                    if(hours == 0.0)
                        velocity = 0.0;
                    else {
                        velocity = polylineLength / hours;
                        if(velocity < 2.5)
                            velocity = 0.0;
                    }
                }

                setLocation(latitude, longitude);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        setSpeed(velocity);
                        updateScreen();

                        if(!town.equals("Buscando ciudad...")) {
                            if (!isFirstLocation) {
                                mapFragment.refreshMap(actualLatitude, actualLongitude, address);
                            } else {
                                mapFragment.startTrip(actualLatitude, actualLongitude, address);
                                isFirstLocation = false;
                            }

                            if (BiggerMapFragment.isInstanceInitialized()) {
                                if (!isFirstLocation2)
                                    biggerMapFragment.refreshMap(actualLatitude, actualLongitude, address);
                                else {
                                    biggerMapFragment = BiggerMapFragment.getInstance();
                                    biggerMapFragment.startTrip(actualLatitude, actualLongitude, address);
                                    isFirstLocation2 = false;
                                }
                            } else {
                                biggerMapFragment = new BiggerMapFragment();
                            }
                        }
                    }
                }, 2000);

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

        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, locationListener);
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, locationListener);

    }

    public void setLocation(double latitude, double longitude) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud

        //Log.d("GPS","Seteo localización");

        if (latitude != 0.0 && longitude != 0.0) {

            new LocationGetter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            //Log.d("GPS","Obtengo dirección");

        }
    }

    public void updateScreen(){
        if(!town.equals("Buscando ciudad...")) {
            MainMenuActivity.cityText.setText(getTown() + ", " + getState());
            MainMenuFragment.mainStreet.setText(getAddress());
            MainMenuFragment.speed.setText(getSpeed());
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

    private class LocationGetter extends AsyncTask<Void, Void, Void> {

        private Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            boolean gotAddress;

            // best effort zoom
            try {
                if (geocoder != null) {
                    List<Address> list = geocoder.getFromLocation(actualLatitude, actualLongitude, 1);
                    if (list != null && !list.isEmpty()) {
                        Address first_address = list.get(0);
                        address = first_address.getAddressLine(0);
                        town = first_address.getLocality();
                        state = first_address.getAddressLine(2);
                        gotAddress = true;
                        //Log.d("GPS","geocoder ok");
                    }else{
                        gotAddress = false;
                    }
                } else {
                    Log.e("GPS", "geocoder was null, is the module loaded?");
                    gotAddress = false;
                }

            } catch (IOException e) {
                Log.e("GPS", "geocoder failed, moving on to HTTP");
                gotAddress = false;
            }

            // try HTTP lookup to the maps API
            if (!gotAddress) {

                try {

                    //Log.d("GPS","http://maps.googleapis.com/maps/api/geocode/json?latlng=" + ((Double)actualLatitude).toString() + "," + ((Double)actualLongitude).toString() + "&sensor=true");

                    ParserJson parser_Json = new ParserJson();
                    JSONObject jsonObj = parser_Json.getJSONfromURL("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + ((Double)actualLatitude).toString() + ","
                            + ((Double)actualLongitude).toString() + "&sensor=true");
                    String Status = jsonObj.getString("status");

                    if (Status.equalsIgnoreCase("OK")) {
                        JSONArray Results = jsonObj.getJSONArray("results");
                        JSONObject zero = Results.getJSONObject(0);
                        JSONArray address_components = zero.getJSONArray("address_components");

                        for (int i = 0; i < address_components.length(); i++) {
                            JSONObject zero2 = address_components.getJSONObject(i);
                            String long_name = zero2.getString("long_name");
                            JSONArray mtypes = zero2.getJSONArray("types");
                            String Type = mtypes.getString(0);

                            if (TextUtils.isEmpty(long_name) == false || !long_name.equals(null) || long_name.length() > 0 || long_name != "") {

                                if (Type.equalsIgnoreCase("street_number")) {
                                    //Log.d("GPS","calle nro");
                                    address = long_name;
                                } else if (Type.equalsIgnoreCase("route")) {
                                    //Log.d("GPS","calle nombre");
                                    address = long_name + " " + address ;
                                    //} else if (Type.equalsIgnoreCase("sublocality")) {
                                    //Address2 = long_name;
                                } else if (Type.equalsIgnoreCase("locality")) {
                                    //Log.d("GPS","ciudad");
                                    // Address2 = Address2 + long_name + ", ";
                                    town = long_name;
                                    //} else if (Type.equalsIgnoreCase("administrative_area_level_2")) {
                                    //County = long_name;
                                } else if (Type.equalsIgnoreCase("administrative_area_level_1")) {
                                    //Log.d("GPS","provincia");
                                    state = long_name;
                                    //} else if (Type.equalsIgnoreCase("country")) {
                                    //Country = long_name;
                                    //} else if (Type.equalsIgnoreCase("postal_code")) {
                                    //PIN = long_name;
                                }
                            }

                            // JSONArray mtypes = zero2.getJSONArray("types");
                            // String Type = mtypes.getString(0);
                            // Log.e(Type,long_name);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }

        public class ParserJson {

            public ParserJson(){}

            public JSONObject getJSONfromURL(String url) {

                // initialize
                InputStream is = null;
                String result = "";
                JSONObject jObject = null;

                //Log.d("GPS-Async","initialize");

                // http post
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(url);
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();

                    //Log.d("GPS-Async","http post");

                } catch (Exception e) {
                    //Log.e("log_tag", "Error in http connection " + e.toString());
                }

                // convert response to string
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    result = sb.toString();
                    //Log.d("GPS-Async","response to string");
                } catch (Exception e) {
                    Log.e("log_tag", "Error converting result " + e.toString());
                }

                // try parse the string to a JSON object
                try {
                    jObject = new JSONObject(result);
                    //Log.d("GPS","parsed object");
                } catch (JSONException e) {
                    Log.e("log_tag", "Error parsing data " + e.toString());
                }

                return jObject;
            }

        }
    }

    public void stopLocationSender(){
        mlocManager.removeUpdates(locationListener);
        mlocManager = null;
    }
}
