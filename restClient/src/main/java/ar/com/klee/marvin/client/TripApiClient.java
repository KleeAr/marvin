package ar.com.klee.marvin.client;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Cookie;

import ar.com.klee.marvin.client.api.UserApi;
import ar.com.klee.marvin.client.model.TripRepresentation;

public class TripApiClient extends AbstractApiClient {

	protected TripApiClient(Client client, Cookie marvinSession, Long userId) {
		super(client, marvinSession, userId);
	}
	
	public TripRepresentation create(TripRepresentation trip) {
		UserApi resourceClient = getResourceClient();
		return resourceClient.createTrip(trip, Arrays.asList(getMarvinSession()));
	}
	
	public TripRepresentation update(TripRepresentation trip) {
		UserApi resourceClient = getResourceClient();
		return resourceClient.createTrip(trip, Arrays.asList(getMarvinSession()));
	}
	
	public List<TripRepresentation> get() {
		UserApi resourceClient = getResourceClient();
		return resourceClient.getTrips(Arrays.asList(getMarvinSession()));
	}
	
	public void delete(String name) {
		UserApi resourceClient = getResourceClient();
		resourceClient.deleteTrip(name, Arrays.asList(getMarvinSession()));
	}

}
