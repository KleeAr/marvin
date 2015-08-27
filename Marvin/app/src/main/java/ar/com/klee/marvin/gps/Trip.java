package ar.com.klee.marvin.gps;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

public class Trip {

    private LatLng beginning;
    private LatLng ending;
    private String beginningAddress;
    private String endingAddress;
    private Date startTime;
    private Date finishTime;
    private String distance;
    private String time;
    private String averageVelocity;
    private List<TripStep> tripPath;

    public Trip(double lat, double lon, String address){
        beginning = new LatLng(lat,lon);
        startTime = new Date();
        beginningAddress = address;
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

    public String getBeginningAddress() {
        return beginningAddress;
    }

    public void setBeginningAddress(String beginningAddress) {
        this.beginningAddress = beginningAddress;
    }

    public String getEndingAddress() {
        return endingAddress;
    }

    public void setEndingAddress(String endingAddress) {
        this.endingAddress = endingAddress;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAverageVelocity() {
        return averageVelocity;
    }

    public void setAverageVelocity(String averageVelocity) {
        this.averageVelocity = averageVelocity;
    }

    public List<TripStep> getTripPath() {
        return tripPath;
    }

    public void setTripPath(List<TripStep> tripPath) {
        this.tripPath = tripPath;
    }

    @Override
    public String toString() {

        String path = "";

        path += tripPath.get(0).getCoordinates().latitude + "," + tripPath.get(0).getCoordinates().longitude;

        for(int i=1; i<tripPath.size(); i++){
            path += ";";
            path += tripPath.get(i).getCoordinates().latitude + "," + tripPath.get(i).getCoordinates().longitude;
        }

        return beginning + ";" +
                ending + ";" +
                startTime + ";" +
                finishTime + ";" +
                distance + ";" +
                time + ";" +
                averageVelocity + "\n" +
                path
                ;
    }
}
