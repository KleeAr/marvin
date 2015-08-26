package ar.com.klee.marvin.client;

import java.util.ArrayList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

import ar.com.klee.marvin.api.UserApi;

public class MarvinClient {
	
	
	private static final ArrayList<Cookie> EMPTY_LIST_COOKIES = new ArrayList<Cookie>();
	private static final Form EMPTY_FORM = new Form();

	public UserApi users() {
		Client client = ClientBuilder.newClient();
		MultivaluedMap<String, Object> headers = new MultivaluedHashMap<String, Object>();
		return WebResourceFactory.newResource(UserApi.class, client.target("http://localhost:8080"), false, headers, EMPTY_LIST_COOKIES, EMPTY_FORM);
	}
	
	public UserApi users(String user, String password) {
		Client client = ClientBuilder.newClient();
		MultivaluedMap<String, Object> headers = new MultivaluedHashMap<String, Object>();
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(user, password);
		client.register(feature);
		return WebResourceFactory.newResource(UserApi.class, client.target("http://localhost:8080"), false, headers, EMPTY_LIST_COOKIES, EMPTY_FORM);
	}
}
