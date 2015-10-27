package ar.com.klee.marvin.gps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
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

import ar.com.klee.marvin.R;
import ar.com.klee.marvin.activities.CallHistoryActivity;
import ar.com.klee.marvin.activities.CameraActivity;
import ar.com.klee.marvin.activities.MainMenuActivity;
import ar.com.klee.marvin.activities.MapActivity;
import ar.com.klee.marvin.activities.SMSInboxActivity;
import ar.com.klee.marvin.configuration.UserConfig;
import ar.com.klee.marvin.fragments.MainMenuFragment;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.STTService;

public class LocationSender implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private String address = "Buscando calle...";
    private String town = "Buscando ciudad...";
    private String state = "";


    private double actualLatitude = 0.0;
    private double actualLongitude = 0.0;
    private String actualAddress;
    private Date actualTime;
    private double previousLatitude = 0.0;
    private double previousLongitude = 0.0;
    private Date previousTime;
    private double startLatitude = 0.0;
    private double startLongitude = 0.0;
    private String startAddress;

    private double velocity = 0.0;
    private Integer speed1 = 0;
    private Integer speed2 = 0;

    private int discardLocation = 0;

    private MapFragment mapFragment;
    private BiggerMapFragment biggerMapFragment;
    private boolean isFirstLocation = true;
    private boolean isFirstLocation2 = true;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private Context context;

    private static LocationSender instance;

    private Thread tUpdateScreen = null;

    private boolean connectionProblemsToast = false;
    private boolean readyToUpdate = false;

    private int speedAlertActivated = 0;
    private boolean enableAppToOpen = false;
    private int zeroCounter = 0;
    private boolean wrongCoordinates = false;

    private int counter = 0;
    private int errorCounter = 0;

    public static LocationSender getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Instance not initialized. Call initializeInstance before calling getInstance");
        }
        return instance;
    }

    public static boolean isInstanceInitialized() {
        return instance != null;
    }

    public LocationSender(final Context context, MapFragment mf){
        /* Use the LocationManager class to obtain GPS locations */

        instance = this;


        this.context = context;

        mapFragment = mf;

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);

        mGoogleApiClient.connect();

        tUpdateScreen = new Thread() {

            @Override
            public void run() {
                try {

                    Thread.sleep(5000);

                    while (!isInterrupted()) {

                        if(!town.equals("Buscando ciudad...") && CommandHandlerManager.isInstanceInitialized()) {

                            boolean size = true;

                            try {
                                size = CommandHandlerManager.getInstance().getMainActivity().getResources().getBoolean(ar.com.klee.marvin.R.bool.isTablet);
                            }catch(Exception e){
                                e.printStackTrace();
                            }

                            final boolean tabletSize = size;

                            CommandHandlerManager.getInstance().getMainActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(!wrongCoordinates) {

                                        String cityText;

                                        if (!tabletSize) {
                                            cityText = getTown() + ", " + getAddressState();
                                            if(cityText.length() > 25){
                                                cityText = cityText.substring(0,21) + "...";
                                            }
                                        }else {
                                            cityText = getTown() + ", " + getAddressState();
                                            if(cityText.length() > 35){
                                                cityText = cityText.substring(0,31) + "...";
                                            }
                                        }

                                        MainMenuActivity.cityText.setText(cityText);

                                        MainMenuFragment.mainStreet.setText(getAddress());

                                        speed1 = speed2;
                                        speed2 = (int) velocity;

                                        Integer maxDifference = 15;

                                        if (actualTime != null && previousTime != null) {
                                            long diffInMs = actualTime.getTime() - previousTime.getTime();
                                            Long seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMs);

                                            if (seconds <= 1)
                                                maxDifference = 11;
                                            else if (seconds <= 3)
                                                maxDifference = 25;
                                            else if (seconds <= 5)
                                                maxDifference = 41;
                                            else
                                                maxDifference = 61;
                                        }

                                        if (speedAlertActivated == 1) {
                                            ((MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity()).speedAlertFinish();
                                            speedAlertActivated = 0;
                                        }

                                        if (speed2 >= 35) {
                                            enableAppToOpen = true;
                                        }

                                        if (speed2 <= 5)
                                            zeroCounter++;
                                        else
                                            zeroCounter = 0;

                                        if (speed1 <= speed2) {
                                            if (speed1 + maxDifference <= speed2) {
                                                speed2 = speed1 + maxDifference;
                                                MainMenuFragment.speed.setText(speed2.toString());
                                                favApp();
                                                speedAlert();
                                            } else {
                                                MainMenuFragment.speed.setText(speed2.toString());
                                                favApp();
                                                speedAlert();
                                            }
                                        } else {
                                            if (speed1 - maxDifference > speed2) {
                                                speed2 = speed1 - maxDifference;
                                                MainMenuFragment.speed.setText(speed2.toString());
                                                favApp();
                                                speedAlert();
                                            } else {
                                                MainMenuFragment.speed.setText(speed2.toString());
                                                favApp();
                                                speedAlert();
                                            }
                                        }
                                    }else{
                                        wrongCoordinates = false;
                                    }
                                }
                            });

                        }else{
                            if(CommandHandlerManager.isInstanceInitialized() && readyToUpdate && !connectionProblemsToast) {
                                try {
                                    errorCounter++;
                                    if(errorCounter == 10) {
                                        CommandHandlerManager.getInstance().getMainActivity().runOnUiThread(new Runnable() {
                                            public void run() {
                                                Toast.makeText(context, "No hay conexión a internet. No se puede identificar el nombre de la calle.", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        connectionProblemsToast = true;
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }

                        Thread.sleep(1000);
                    }

                } catch (InterruptedException e) {
                } catch (NullPointerException e) {
                }
            }
        };

        tUpdateScreen.start();

    }

    @Override
    public void onLocationChanged(Location location) {
        if(discardLocation == 2)
            handleNewLocation(location);
        else
            discardLocation++;
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void handleNewLocation(Location location) {

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        /*
        Log.d("GPS",((Integer)counter).toString());
        if(counter%10 == 8){
            latitude = -34.614397;
            longitude = -58.373169;
        }else if(counter%10 == 9){
            latitude = -34.614397;
            longitude = -58.373169;
        }
        counter++;
        */

        //Log.d("GPS","Recibo nuevas coordenadas. Latitud = " + ((Double)latitude).toString() + "; Longitud = " + ((Double)longitude).toString());

        double latitudeBackUp = previousLatitude;
        double longitudeBackUp = previousLongitude;
        Date timeBackUp = previousTime;

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

            if(polylineLength > 100.0) {
                wrongCoordinates = true;

                actualLatitude = previousLatitude;
                actualLongitude = previousLongitude;
                actualTime = previousTime;

                previousLatitude = latitudeBackUp;
                previousLongitude = longitudeBackUp;
                previousTime = timeBackUp;

                return;
            }

            polylineLength = polylineLength / 1000;

            if(hours == 0.0)
                velocity = 0.0;
            else {
                velocity = polylineLength / hours;
                if(velocity < 3.0)
                    velocity = 0.0;
            }
        }

        setLocation(latitude, longitude);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                readyToUpdate = true;

                if (!town.equals("Buscando ciudad...")) {
                    if (!isFirstLocation) {
                        mapFragment.refreshMap(actualLatitude, actualLongitude, address);
                    } else {
                        mapFragment.startTrip(actualLatitude, actualLongitude, address);
                        isFirstLocation = false;
                    }

                    if (BiggerMapFragment.isInstanceInitialized()) {
                        if (!isFirstLocation2) {
                            biggerMapFragment.refreshMap(actualLatitude, actualLongitude, address);
                            actualAddress = address;
                            if(startLatitude == 0.0){
                                startLatitude = actualLatitude;
                                startLongitude = actualLongitude;
                                startAddress = address;
                            }
                        }else {
                            biggerMapFragment = BiggerMapFragment.getInstance();
                            startLatitude = actualLatitude;
                            startLongitude = actualLongitude;
                            startAddress = address;
                            isFirstLocation2 = false;
                        }
                    } else {
                        biggerMapFragment = new BiggerMapFragment();
                    }
                }
            }
        }, 2000);
    }

    public void stopLocationSender(){
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
            mGoogleApiClient.disconnect();
        }
        tUpdateScreen.interrupt();
    }

    public void setLocation(double latitude, double longitude) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (latitude != 0.0 && longitude != 0.0) {

            new LocationGetter().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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

    public String getAddressState(){
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
                            + ((Double)actualLongitude).toString() + "&sensor=true&language=es");
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
                    if(!connectionProblemsToast) {
                        e.printStackTrace();
                    }
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

    public void openApp(String app){

        CommandHandlerManager commandHandlerManager = CommandHandlerManager.getInstance();

        Context context = CommandHandlerManager.getInstance().getContext();

        if(app.equals("marvin - cámara")) {
            enableAppToOpen = false;
            zeroCounter = 0;
            Intent intent = new Intent(context, CameraActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        }else if(app.equals("marvin - historial de sms")) {
            enableAppToOpen = false;
            zeroCounter = 0;
            Intent intent = new Intent(context, SMSInboxActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        }else if(app.equals("marvin - historial de llamadas")) {
            enableAppToOpen = false;
            zeroCounter = 0;
            Intent intent = new Intent(context, CallHistoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        }else if(app.equals("marvin - mapa")) {
            enableAppToOpen = false;
            zeroCounter = 0;
            Intent intent = new Intent(context, MapActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return;
        }else if(app.equals("marvin - mis sitios")) {
            enableAppToOpen = false;
            zeroCounter = 0;
            ((MainMenuActivity)commandHandlerManager.getMainActivity()).previousMenus.push(1);
            ((MainMenuActivity)commandHandlerManager.getMainActivity()).setFragment(5);
            return;
        }else if(app.equals("marvin - historial de viajes")) {
            enableAppToOpen = false;
            zeroCounter = 0;
            ((MainMenuActivity)commandHandlerManager.getMainActivity()).previousMenus.push(1);
            ((MainMenuActivity)commandHandlerManager.getMainActivity()).setFragment(4);
            return;
        }else if(app.equals("marvin - dónde estacioné")) {
            enableAppToOpen = false;
            zeroCounter = 0;
            ((MainMenuActivity)commandHandlerManager.getMainActivity()).previousMenus.push(1);
            ((MainMenuActivity)commandHandlerManager.getMainActivity()).setFragment(6);
            return;
        }else{
            final PackageManager pm = CommandHandlerManager.getInstance().getContext().getPackageManager();
            //get a list of installed apps.
            List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

            int i = 0;

            while(i < packages.size()) {
                String appLabel = pm.getApplicationLabel(packages.get(i)).toString();

                if(appLabel.toLowerCase().equals(app)){
                    zeroCounter = 0;
                    enableAppToOpen = false;
                    commandHandlerManager.getMainActivity().startActivity(pm.getLaunchIntentForPackage(packages.get(i).packageName));
                    return;
                }

                i++;
            }
        }
    }

    public void favApp(){

        if (enableAppToOpen && zeroCounter >= 3 && UserConfig.getSettings().isOpenAppWhenStop() && speed2 < 1) {

            String app = UserConfig.getSettings().getAppToOpenWhenStop();
            CommandHandlerManager commandHandlerManager = CommandHandlerManager.getInstance();

            if (STTService.isInstanceInitialized() && !STTService.getInstance().getIsListening() &&
                    commandHandlerManager.getCurrentActivity() == CommandHandlerManager.ACTIVITY_MAIN &&
                    commandHandlerManager.getCommandHandler() == null) {

                app = app.toLowerCase();

                openApp(app);
            }
        }
    }

    public void speedAlert(){
        if (speedAlertActivated == 0 && STTService.isInstanceInitialized() && UserConfig.getSettings().isSpeedAlertEnabled() && speed2 > UserConfig.getSettings().getAlertSpeed()) {
            speedAlertActivated = 2;
            CommandHandlerManager commandHandlerManager = CommandHandlerManager.getInstance();
            STTService.getInstance().setIsListening(false);
            STTService.getInstance().stopListening();
            commandHandlerManager.setNullCommand();
            ((MainMenuActivity) CommandHandlerManager.getInstance().getMainActivity()).speedAlert();
            commandHandlerManager.getTextToSpeech().speakText("SPEED ALERT - Superaste los " + UserConfig.getSettings().getAlertSpeed() + " kilometros por hora");
        }
    }

    public double getStartLatitude() {
        return startLatitude;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public double getActualLatitude() {
        return actualLatitude;
    }

    public double getActualLongitude() {
        return actualLongitude;
    }

    public String getActualAddress() {
        return actualAddress;
    }

    public void setSpeedAlertActivated(int speedAlertActivated) {
        this.speedAlertActivated = speedAlertActivated;
    }

    public void setEnableAppToOpen(boolean enableAppToOpen) {
        this.enableAppToOpen = enableAppToOpen;
    }
}
