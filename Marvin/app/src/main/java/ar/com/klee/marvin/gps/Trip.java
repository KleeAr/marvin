package ar.com.klee.marvin.gps;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public class Trip {

    private LatLng beginning;
    private LatLng ending;
    private Date startTime;
    private Date finishTime;
    private String distance;
    private long time;
    private String averageVelocity;

    public Trip(double lat, double lon){
        beginning = new LatLng(lat,lon);
        finishTime = new Date();
    }

    public LatLng getBeginning() {
        return beginning;
    }

    public void setBeginning(LatLng beginning) {
        this.beginning = beginning;
    }

    public LatLng getEnding() {
        return ending;
    }

    public void setEnding(LatLng ending) {
        this.ending = ending;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAverageVelocity() {
        return averageVelocity;
    }

    public void setAverageVelocity(String averageVelocity) {
        this.averageVelocity = averageVelocity;
    }
}
