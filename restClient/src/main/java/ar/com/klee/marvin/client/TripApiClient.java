package ar.com.klee.marvin.client;

import java.util.Arrays;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Cookie;

import ar.com.klee.marvin.api.UserApi;
import ar.com.klee.marvin.model.Trip;

public class TripApiClient extends AbstractApiClient {

	protected TripApiClient(Client client, Cookie marvinSession, Long userId) {
		super(client, marvinSession, userId);
	}
	
	public Trip create(String name, String coordenates) {
		UserApi resourceClient = getResourceClient();
		return resourceClient.createTrip(new Trip(null, name, coordenates, null), Arrays.asList(getMarvinSession()));
	}
	
	public Trip update(String name, String coordenates) {
		UserApi resourceClient = getResourceClient();
		return resourceClient.createTrip(new Trip(null, name, coordenates, null), Arrays.asList(getMarvinSession()));
	}
	
	public void delete(String name) {
		UserApi resourceClient = getResourceClient();
		resourceClient.deleteTrip(name, Arrays.asList(getMarvinSession()));
	}

}
