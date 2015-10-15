package ar.com.klee.marvin.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(TripKey.class)
public class Site {
	
	@Id
	private Long userId;
    @Id
	private String name;
    private String address;
    private Double lat;
    private Double lng;
    private int thumbnail;
    private String image;

    public Site() {
	}
    
    public Site(Long userId, String name, String address, Double lat, Double lng, int thumbnail){
        this.name = name;
        this.address = address;
        this.thumbnail = thumbnail;
        this.lat = lat;
        this.lng = lng;
    }

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public int getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(int thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String siteImage) {
		this.image = siteImage;
	}

   
}