package ar.com.klee.marvin.model;

import java.io.Serializable;

public class UserSettingKey implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String key;
	private Long userId;
	
	public UserSettingKey() {
		super();
	}

	public UserSettingKey(String key, Long userId) {
		super();
		this.key = key;
		this.userId = userId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
}