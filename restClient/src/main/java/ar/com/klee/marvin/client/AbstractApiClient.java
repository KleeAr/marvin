package ar.com.klee.marvin.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Cookie;

import org.glassfish.jersey.client.proxy.WebResourceFactory;

import com.owlike.genson.Genson;

import ar.com.klee.marvin.api.UserApi;

public class AbstractApiClient {

	private Client client;
	private Cookie marvinSession;
	private Long userId;
	private Genson genson = new Genson();
	

	public AbstractApiClient(Client client) {
		this.client = client;
	}
	
	public AbstractApiClient(Client client, Cookie marvinSession) {
		super();
		this.client = client;
		this.marvinSession = marvinSession;
	}

	public AbstractApiClient(Client client, Cookie marvinSession, Long userId) {
		this(client, marvinSession);
		this.userId = userId;
	}

	protected Client getClient() {
		return client;
	}

	protected void setClient(Client client) {
		this.client = client;
	}

	protected Cookie getMarvinSession() {
		return marvinSession;
	}

	protected void setMarvinSession(Cookie marvinSession) {
		this.marvinSession = marvinSession;
	}

	protected Long getUserId() {
		return userId;
	}

	protected void setUserId(Long userId) {
		this.userId = userId;
	}

	protected UserApi getResourceClient() {
		return WebResourceFactory.newResource(UserApi.class, getClient().target("http://localhost:8080"));
	}
	
	protected Genson getGenson() {
		return genson;
	}
}
