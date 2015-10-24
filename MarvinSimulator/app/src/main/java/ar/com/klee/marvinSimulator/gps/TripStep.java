package ar.com.klee.marvinSimulator.gps;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

public class TripStep {

    private final double lon;
    private final double lat;
    private LatLng coordinates;
    private String address;

    public TripStep(double lat, double lon, String address){
        coordinates = new LatLng(lat,lon);
        this.lat = lat;
        this.lon = lon;
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

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}

