package ar.com.klee.marvin.gps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class IntersectionSender {

    private double latitude;
    private double longitude;
    private double previousLatitude;
    private double previousLongitude;
    private Context context;
    public static final int NEXT_STREET = 1;
    public static final int PREVIOUS_STREET = 2;

    public IntersectionSender(double lat1, double lon1, double lat2, double lon2, Context context){

        this.latitude = lat1;
        this.longitude = lon1;
        this.previousLatitude = lat2;
        this.previousLongitude = lon2;
        this.context = context;

    }

    public double getLatitudeDifference(){

        return latitude - previousLatitude;

    }

    public double getLongitudeDifference(){

        return longitude - previousLongitude;

    }

    public String findStreet(int type){

        if(type != NEXT_STREET && type != PREVIOUS_STREET)
            return null;

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {

            List<Address> list = geocoder.getFromLocation(latitude, longitude, 1);
            String actualStreet, nextStreet, nextLeft, nextRight;
            actualStreet = "";

            //Obtenemos la dirección actual
            if (!list.isEmpty()) {
                Address address = list.get(0);
                actualStreet = address.getThoroughfare();
            }

            if(actualStreet.equals(""))
                return "";

            double newLatitudeRight, newLongitudeRight, newLatitudeLeft, newLongitudeLeft;
            double setLatitudeRight, setLongitudeRight, setLatitudeLeft, setLongitudeLeft;

            //Obtenemos la siguiente calle por un lado
            setLatitudeRight = latitude - (0.0003) * (getLatitudeDifference()/getLatitudeDifference() * (-1));
            setLongitudeRight = longitude - (0.0003) * (getLongitudeDifference()/getLongitudeDifference() * (-1));
            setLatitudeLeft = latitude + (0.0003) * (getLatitudeDifference()/getLatitudeDifference() * (-1));
            setLongitudeLeft = longitude + (0.0003) * (getLongitudeDifference()/getLongitudeDifference() * (-1));
            nextRight = actualStreet;
            nextLeft = actualStreet;
            int i = 0;
            while((nextRight.equals(actualStreet) || nextLeft.equals(actualStreet)) && i<10){

                if(type == NEXT_STREET) {
                    newLatitudeRight = setLatitudeRight + getLatitudeDifference();
                    newLongitudeRight = setLongitudeRight + getLongitudeDifference();
                    newLatitudeLeft = setLatitudeLeft + getLatitudeDifference();
                    newLongitudeLeft = setLongitudeLeft + getLongitudeDifference();
                }else{
                    newLatitudeRight = setLatitudeRight - getLatitudeDifference();
                    newLongitudeRight = setLongitudeRight - getLongitudeDifference();
                    newLatitudeLeft = setLatitudeLeft - getLatitudeDifference();
                    newLongitudeLeft = setLongitudeLeft - getLongitudeDifference();
                }

                if(nextRight.equals(actualStreet)) {
                    list = geocoder.getFromLocation(newLatitudeRight, newLongitudeRight, 1);
                    if (!list.isEmpty()) {
                        Address address = list.get(0);
                        nextRight = address.getThoroughfare();
                    }
                }

                if(nextLeft.equals(actualStreet)) {
                    list = geocoder.getFromLocation(newLatitudeLeft, newLongitudeLeft, 1);
                    if (!list.isEmpty()) {
                        Address address = list.get(0);
                        nextLeft = address.getThoroughfare();
                    }
                }

                setLatitudeRight = newLatitudeRight;
                setLongitudeRight = newLongitudeRight;
                setLatitudeLeft = newLatitudeLeft;
                setLongitudeLeft = newLongitudeLeft;

                i++;

                Log.d("RIGHT", nextRight);
                Log.d("LEFT",nextLeft);
            }

            if(nextLeft.equals(""))
                nextStreet = nextRight;
            else if(nextRight.equals(""))
                nextStreet = nextLeft;
            else if(nextLeft.equals(nextRight))
                nextStreet = nextRight;
            else
                nextStreet = nextLeft + " y " + nextRight;

            return nextStreet;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}
