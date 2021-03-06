package ar.com.klee.marvin.model;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Embeddable
public class TripStep {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private Double lat;
	private Double lon;
	private String address;

	public TripStep() {
	}
	
	public TripStep(Long id, Double lat, Double lon, String address) {
		super();
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		this.address = address;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	
	
}
