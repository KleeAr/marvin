package ar.com.klee.marvin.client;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Cookie;

import ar.com.klee.marvin.api.UserApi;
import ar.com.klee.marvin.model.LoginRequest;
import ar.com.klee.marvin.model.LoginResponse;
import ar.com.klee.marvin.model.User;

public class UserApiClient extends AbstractApiClient {

	public UserApiClient(Client client) {
		super(client);
	}
	
	public void authenticate(String email, String password) {
		UserApi resourceClient = getResourceClient();
		LoginResponse response = resourceClient.authenticate(new LoginRequest(email, password));
		setUserId(response.getUserId());
		createMarvinSession(response);
	}
	
	private void createMarvinSession(LoginResponse response) {
		setMarvinSession(new Cookie("JSESSIONID", response.getSessionId(), "/", "localhost", 1));
	}

	public User register(User user) {
		UserApi resourceClient = getResourceClient();
		return resourceClient.register(user);
	}

	public List<User> getAll() {
 		UserApi resourceClient = getResourceClient();
		return resourceClient.getAll(Arrays.asList(getMarvinSession()));
	}
	
	public SettingApiClient settings() {
		return new SettingApiClient(getClient(), getMarvinSession(), getUserId());
	}
	
}
