package ar.com.klee.marvin.client;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import ar.com.klee.marvin.api.UserApi;
import ar.com.klee.marvin.model.User;

public class MarvinClientTest {

	private static final String PASSWORD = "matias";
	private static final String MAIL = "matias@gmail.com";
	MarvinClient client = new MarvinClient();
	
	@Test
	public void testUsers() {
		UserApi userApi = client.users(MAIL, PASSWORD);
		userApi.register(new User(null, "Matías", "Salerno", MAIL, PASSWORD));
		List<User> users = userApi.getAll();
		assertFalse(users.isEmpty());
	}

}
