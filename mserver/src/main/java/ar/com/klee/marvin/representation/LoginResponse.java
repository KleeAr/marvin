package ar.com.klee.marvin.representation;

import ar.com.klee.marvin.model.UserSetting;

public class LoginResponse {

	private Long userId;
	private String sessionId;
	private UserSetting settings;
	private String email;
	private String firstName;
	private String lastName;
	

	public LoginResponse() {
	}
	
	public LoginResponse(Long userId, String sessionId, UserSetting settings, String firstName, String lastName, String email) {
		this.userId = userId;
		this.sessionId = sessionId;
		this.settings = settings;
		this.email = email;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public void setSettings(UserSetting settings) {
		this.settings = settings;
	}
	
	public UserSetting getSettings() {
		return settings;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	

}