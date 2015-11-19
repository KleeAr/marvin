package ar.com.klee.marvin.representation;

public class ChangePasswordRequest {

	private String oldPassword;
	private String password;
	
	public ChangePasswordRequest() {
	}
	
	public ChangePasswordRequest(String oldPassword, String password) {
		super();
		this.oldPassword = oldPassword;
		this.password = password;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
