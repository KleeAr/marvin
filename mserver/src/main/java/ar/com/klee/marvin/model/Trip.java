package ar.com.klee.marvin.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Trip {

	@Id
	private String name;
	@Id
	private Long userId;
	private String coordenates;
	
	
	public Trip() {
		super();
	}

	public Trip(Long id, String name, String coordenates, Long userId) {
		super();
		this.name = name;
		this.coordenates = coordenates;
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCoordenates() {
		return coordenates;
	}

	public void setCoordenates(String coordenates) {
		this.coordenates = coordenates;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
}
