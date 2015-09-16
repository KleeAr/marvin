package ar.com.klee.marvin.client.model;

import java.util.ArrayList;
import java.util.List;

public class TripRepresentation {

	private String name;
	private Long userId;
	private Double lat;
	private Double lon;
	private String address;
	private String averageVelocity;
	private String time;
	private List<TripStepRepresentation> tripPath = new ArrayList<TripStepRepresentation>();
	
	
	public TripRepresentation() {
		super();
	}


	public TripRepresentation(String name, Long userId, Double lat, Double lon, String address, String averageVelocity, String time,
			List<TripStepRepresentation> tripPath) {
		super();
		this.name = name;
		this.userId = userId;
		this.lat = lat;
		this.lon = lon;
		this.address = address;
		this.averageVelocity = averageVelocity;
		this.time = time;
		this.tripPath = tripPath;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public Double getLat() {
		return lat;
	}


	public void setLat(Double lat) {
		this.lat = lat;
	}


	public Double getLon() {
		return lon;
	}


	public void setLon(Double lon) {
		this.lon = lon;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getAverageVelocity() {
		return averageVelocity;
	}


	public void setAverageVelocity(String averageVelocity) {
		this.averageVelocity = averageVelocity;
	}


	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


	public List<TripStepRepresentation> getTripPath() {
		return tripPath;
	}


	public void setTripPath(List<TripStepRepresentation> tripPath) {
		this.tripPath = tripPath;
	}

	
}
