package ar.com.klee.marvin.model;


public class LoginResponse {

	private Long userId;

	public LoginResponse() {
	}
	
	public LoginResponse(Long userId) {
		this.userId = userId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
