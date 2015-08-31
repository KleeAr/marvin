package ar.com.klee.marvin.client;

import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;

import ar.com.klee.marvin.model.User;

public class MarvinClientTest {

	private static final String PASSWORD = "fede";
	private static final String MAIL = "fede@gmail.com";
	MarvinClient client = new MarvinClient();
	
	@Test
	public void testUsers() {
		UserApiClient usersApi = client.users();
//		usersApi.register(new User(null, "Fede", "Sinopoli", MAIL, PASSWORD));
		usersApi.authenticate(MAIL, PASSWORD);
		List<User> users = usersApi.getAll();
		assertFalse(users.isEmpty());
	}

}
