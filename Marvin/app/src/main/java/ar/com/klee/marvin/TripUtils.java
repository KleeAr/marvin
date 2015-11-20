package ar.com.klee.marvin;

import ar.com.klee.marvin.gps.Trip;

/**
 * @author msalerno
 */
public class TripUtils {

    public static String getTripName(Trip trip) {
        return trip.getEndingAddress() + trip.getFinishTime().getTime();
    }
}
