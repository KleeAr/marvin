package ar.com.klee.marvin.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(UserSettingKey.class)
public class UserSetting {

	@Id
	private String key;
	@Id
	private Long userId;
	private String value;
	
	public UserSetting() {
		
	}
	
	public UserSetting(String key, Long userId, String value) {
		super();
		this.key = key;
		this.userId = userId;
		this.value = value;
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
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	
}