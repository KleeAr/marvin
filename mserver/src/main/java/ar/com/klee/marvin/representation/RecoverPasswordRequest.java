package ar.com.klee.marvin.representation;

public class RecoverPasswordRequest {

	private String email;
	
	public RecoverPasswordRequest() {
	}

	
	public RecoverPasswordRequest(String email) {
		super();
		this.email = email;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
