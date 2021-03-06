package ar.com.klee.marvin.configuration;


import java.util.ArrayList;
import java.util.List;

import ar.com.klee.marvin.client.model.TripRepresentation;
import ar.com.klee.marvin.gps.Site;
import ar.com.klee.marvin.gps.Trip;

public class UserTrips {

    private static UserTrips instance;

    List<Trip> trips = new ArrayList<Trip>();

    public UserTrips(){
        instance = this;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public static UserTrips getInstance() {
        if (instance == null) {
            instance = new UserTrips();
        }
        return instance;

    }

    public void add(Trip trip){
        trips.add(trip);
    }

    public static boolean isInstanceInitialized() {
        return instance != null;
    }

    public static void destroyInstance() {
        instance = null;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public List<TripRepresentation> getTripRepresentations() {
        List<TripRepresentation> tripRepresentations = new ArrayList<>(trips.size());
        for (Trip trip : trips) {
            tripRepresentations.add(trip.toRepresentation());
        }
        return tripRepresentations;
    }
}
