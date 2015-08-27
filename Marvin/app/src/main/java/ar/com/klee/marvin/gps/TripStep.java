package ar.com.klee.marvin.gps;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

public class TripStep {

    private LatLng coordinates;
    private String address;

    public TripStep(double lat, double lon, String address){
        coordinates = new LatLng(lat,lon);
        this.address = address;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

