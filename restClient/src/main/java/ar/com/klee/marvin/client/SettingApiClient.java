package ar.com.klee.marvin.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Cookie;

public class SettingApiClient extends AbstractApiClient {

	public SettingApiClient(Client client, Cookie marvinSession, Long userId) {
		super(client,marvinSession, userId);
	}


}
