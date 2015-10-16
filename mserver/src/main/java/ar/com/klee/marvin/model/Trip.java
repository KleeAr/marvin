package ar.com.klee.marvin.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(TripKey.class)
public class Trip {

	@Id
	private String name;
	@Id
	private Long userId;
    private double beginLat;
    private double beginLon;
    private double endLat;
    private double endLon;
    private String beginningAddress;
    private String endingAddress;
    private Date startTime;
    private Date finishTime;
    private String distance;
    private String time;
    private String averageVelocity;
	@ElementCollection
	@CollectionTable(name = "steps")
    private List<TripStep> tripPath;
	
	public Trip() {
		super();
	}

	public Trip(String name, Long userId, double beginLat, double beginLon,
			double endLat, double endLon, String beginningAddress,
			String endingAddress, Date startTime, Date finishTime,
			String distance, String time, String averageVelocity,
			List<TripStep> tripPath) {
		super();
		this.name = name;
		this.userId = userId;
		this.beginLat = beginLat;
		this.beginLon = beginLon;
		this.endLat = endLat;
		this.endLon = endLon;
		this.beginningAddress = beginningAddress;
		this.endingAddress = endingAddress;
		this.startTime = startTime;
		this.finishTime = finishTime;
		this.distance = distance;
		this.time = time;
		this.averageVelocity = averageVelocity;
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

	public double getBeginLat() {
		return beginLat;
	}

	public void setBeginLat(double beginLat) {
		this.beginLat = beginLat;
	}

	public double getBeginLon() {
		return beginLon;
	}

	public void setBeginLon(double beginLon) {
		this.beginLon = beginLon;
	}

	public double getEndLat() {
		return endLat;
	}

	public void setEndLat(double endLat) {
		this.endLat = endLat;
	}

	public double getEndLon() {
		return endLon;
	}

	public void setEndLon(double endLon) {
		this.endLon = endLon;
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

}
