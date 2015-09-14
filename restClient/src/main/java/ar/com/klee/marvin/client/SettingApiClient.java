package ar.com.klee.marvin.client;

import java.util.Arrays;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Cookie;

import ar.com.klee.marvin.api.UserApi;
import ar.com.klee.marvin.model.UserSetting;

public class SettingApiClient extends AbstractApiClient {

	protected SettingApiClient(Client client, Cookie marvinSession, Long userId) {
		super(client,marvinSession, userId);
	}

	public UserSetting update(UserSetting settings) {
		UserApi resourceClient = getResourceClient();
		return resourceClient.updateSetting(settings, Arrays.asList(getMarvinSession()));
	}
	
}
