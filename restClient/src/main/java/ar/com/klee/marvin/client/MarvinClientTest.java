package ar.com.klee.marvin.client;

import org.junit.Test;

import ar.com.klee.marvin.model.User;

public class MarvinClientTest {

	private static final String PASSWORD = "fede";
	private static final String MAIL = "fede@gmail.com";
	
	@Test
	public void testUsers() {
		Marvin.users().register(new User(null, "Fede", "Sinopoli", MAIL, PASSWORD));
		Marvin.users().authenticate(MAIL, PASSWORD);
	}

}
