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

	public UserSetting create(String key, String value) {
		UserApi resourceClient = getResourceClient();
		return resourceClient.createSetting(new UserSetting(key, null, value), Arrays.asList(getMarvinSession()));
	}
	
	public UserSetting update(String key, String value) {
		UserApi resourceClient = getResourceClient();
		return resourceClient.updateSetting(new UserSetting(key, null, value), Arrays.asList(getMarvinSession()));
	}
	
	public void delete(String key) {
		UserApi resourceClient = getResourceClient();
		resourceClient.deleteSetting(key, Arrays.asList(getMarvinSession()));
	}
}
