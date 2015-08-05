package ar.com.klee.marvin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ar.com.klee.marvin.model.UserSetting;
import ar.com.klee.marvin.model.UserSettingKey;
import ar.com.klee.marvin.repository.UserSettingRepository;

@RestController
@RequestMapping("/users/{id}/settings")
public class UserSettingController {
	
	private UserSettingRepository userSettingRepository;
	
	@Autowired	
	public UserSettingController(UserSettingRepository userSettingRepository) {
		super();
		this.userSettingRepository = userSettingRepository;
	}

	@RequestMapping(method = RequestMethod.GET)
	public Iterable<UserSetting> getAll(@PathVariable("id")Long userId) {
		return userSettingRepository.findByUserId(userId);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public UserSetting save(@RequestBody UserSetting userSetting) {
		return userSettingRepository.save(userSetting);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public UserSetting update(@RequestBody UserSetting userSetting) {
		return userSettingRepository.save(userSetting);
	}
	
	@RequestMapping(value = "/{key}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id")Long userId, @PathVariable("key") String key) {
		userSettingRepository.delete(new UserSettingKey(key, userId));
	}
	
}