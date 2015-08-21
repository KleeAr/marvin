package ar.com.klee.marvin.model;

public class UserSetting {

	private String key;
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