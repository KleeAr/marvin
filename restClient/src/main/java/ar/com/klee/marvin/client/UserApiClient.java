package ar.com.klee.marvin.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Cookie;

import ar.com.klee.marvin.api.UserApi;
import ar.com.klee.marvin.model.LoginRequest;
import ar.com.klee.marvin.model.LoginResponse;
import ar.com.klee.marvin.model.User;

public class UserApiClient extends AbstractApiClient {

	private SettingApiClient settingsApi;
	private TripApiClient tripApi;
	
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

	public SettingApiClient settings() {
		if(settingsApi == null) {
			settingsApi = new SettingApiClient(getClient(), getMarvinSession(), getUserId());
		}
		return settingsApi;
	}
	
	public TripApiClient trips() {
		if(tripApi == null) {
			tripApi = new TripApiClient(getClient(), getMarvinSession(), getUserId());
		}
		return tripApi;
	}
	
}
