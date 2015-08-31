package ar.com.klee.marvin.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class MarvinClient {
	
	
	public UserApiClient users() {
		Client client = ClientBuilder.newClient();
		return new UserApiClient(client);
	}
	
}
