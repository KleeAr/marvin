package ar.com.klee.marvin.client.model;


public class LoginResponse {

	private Long userId;
	private String sessionId;

	public LoginResponse() {
	}
	
	public LoginResponse(Long userId, String sessionId) {
		this.userId = userId;
		this.sessionId = sessionId;
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

}
