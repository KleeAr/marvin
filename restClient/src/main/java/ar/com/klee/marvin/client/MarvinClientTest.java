package ar.com.klee.marvin.client;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import ar.com.klee.marvin.api.UserApi;
import ar.com.klee.marvin.model.User;

public class MarvinClientTest {

	private static final String PASSWORD = "fede";
	private static final String MAIL = "fede@gmail.com";
	MarvinClient client = new MarvinClient();
	
	@Test
	public void testUsers() {
		UserApi userApi = client.users();
		userApi.register(new User(null, "Fede", "Sinopoli", MAIL, PASSWORD));
		userApi = client.users(MAIL, PASSWORD);
		List<User> users = userApi.getAll();
		users = userApi.getAll();
		users = userApi.getAll();
		assertFalse(users.isEmpty());
	}

}
