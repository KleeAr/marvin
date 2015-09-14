package ar.com.klee.marvin.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ar.com.klee.marvin.model.User;
import ar.com.klee.marvin.model.UserSetting;
import ar.com.klee.marvin.repository.UserSettingRepository;

@RestController
public class UserSettingController {
	
	private UserSettingRepository userSettingRepository;
	
	@Autowired	
	public UserSettingController(UserSettingRepository userSettingRepository) {
		super();
		this.userSettingRepository = userSettingRepository;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/users/{id}/settings")
	public Iterable<UserSetting> getAll(@PathVariable("id")Long userId) {
		return userSettingRepository.findByUserId(userId);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/users/{id}/settings")
	public UserSetting save(@RequestBody UserSetting userSetting) {
		return userSettingRepository.save(userSetting);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/users/{id}/settings")
	public UserSetting update(@RequestBody UserSetting userSetting) {
		return userSettingRepository.save(userSetting);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/users/me/settings")
	public UserSetting saveMe(@RequestBody UserSetting userSetting, HttpSession session) {
		User user = (User) session.getAttribute("user");
		userSetting.setUserId(user.getId());
		return userSettingRepository.save(userSetting);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/users/me/settings")
	public UserSetting updateMe(@RequestBody UserSetting userSetting, HttpSession session) {
		User user = (User) session.getAttribute("user");
		userSetting.setUserId(user.getId());
		return userSettingRepository.save(userSetting);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/users/me/settings")
	public Iterable<UserSetting> getAll(HttpSession session) {
		User user = (User) session.getAttribute("user");
		return userSettingRepository.findByUserId(user.getId());
	}
}