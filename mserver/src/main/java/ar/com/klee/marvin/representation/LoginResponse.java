package ar.com.klee.marvin.representation;

import ar.com.klee.marvin.model.UserSetting;

public class LoginResponse {

	private Long userId;
	private String sessionId;
	private UserSetting settings;
	

	public LoginResponse() {
	}
	
	public LoginResponse(Long userId, String sessionId, UserSetting settings) {
		this.userId = userId;
		this.sessionId = sessionId;
		this.settings = settings;
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

}