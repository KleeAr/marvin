package ar.com.klee.marvin.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class Marvin {
	
	private Marvin(){}
	
	private static String marvinHost = "localhost";
	
	public static UserApiClient userApiClient;
	
	public static UserApiClient users() {
		if(userApiClient == null) {
			Client client = ClientBuilder.newClient();
			userApiClient = new UserApiClient(client);
		}
		return userApiClient;
	}

	public static String getMarvinHost() {
		return marvinHost;
	}

	public static void setMarvinHost(String marvinHost) {
		Marvin.marvinHost = marvinHost;
	}
	
}
