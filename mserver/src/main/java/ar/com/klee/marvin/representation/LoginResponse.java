package ar.com.klee.marvin.representation;

import ar.com.klee.marvin.model.User;

public class LoginResponse {

	private Long userId;

	public LoginResponse() {
	}
	
	public LoginResponse(User user) {
		this.userId = user.getId();
	}

	protected Long getUserId() {
		return userId;
	}

	protected void setUserId(Long userId) {
		this.userId = userId;
	}

}
