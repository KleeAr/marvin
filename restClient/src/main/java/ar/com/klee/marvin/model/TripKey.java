package ar.com.klee.marvin.model;

import java.io.Serializable;


public class TripKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String name;
	private Long userId;
	
	public TripKey() {
		
	}
	
	public TripKey(String name, Long userId) {
		super();
		this.name = name;
		this.userId = userId;
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

	
	
}
