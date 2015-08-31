package ar.com.klee.marvin.client;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

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
		Response response = resourceClient.authenticate(new LoginRequest(email, password));
		NewCookie cookie = response.getCookies().get("JSESSIONID");
		LoginResponse loginResponse = getGenson().deserialize((InputStream)response.getEntity(), LoginResponse.class);
		setUserId(loginResponse.getUserId());
		setMarvinSession(new Cookie(cookie.getName(), cookie.getValue(), cookie.getPath(), cookie.getDomain(), cookie.getVersion()));
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
