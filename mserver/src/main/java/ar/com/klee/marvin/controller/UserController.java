package ar.com.klee.marvin.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ar.com.klee.marvin.model.User;
import ar.com.klee.marvin.model.UserSetting;
import ar.com.klee.marvin.repository.UserRepository;
import ar.com.klee.marvin.repository.UserSettingRepository;
import ar.com.klee.marvin.representation.LoginRequest;
import ar.com.klee.marvin.representation.LoginResponse;

@RestController
@RequestMapping(value = "/users")
public class UserController {

	private UserRepository userRepository;
	private UserSettingRepository settingRepository;
	private AuthenticationManager authManager;
	
	@Autowired	
	public UserController(UserRepository userRepository, AuthenticationManager authenticationManager, UserSettingRepository settingRepository) {
		super();
		this.userRepository = userRepository;
		this.authManager = authenticationManager;
		this.settingRepository = settingRepository;
	}

	@RequestMapping(method = RequestMethod.GET)
	public Iterable<User> getAll() {
		return userRepository.findAll();
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public User getById(@PathVariable("id")Long id) {
		return userRepository.findOne(id);
	}
	
	@RequestMapping(value = "/me", method = RequestMethod.GET)
	public User getMe(HttpSession session) {
		return (User) session.getAttribute("user");
	}
	
	@RequestMapping(value ="/register", method = RequestMethod.POST)
	public User save(@RequestBody User user) {
		user = userRepository.save(user);
		settingRepository.save(UserSetting.createDefaultSettings(user));
		return user;
	}
	
	@RequestMapping(value = "/auth", method = RequestMethod.POST)
	public LoginResponse authenticate(@RequestBody LoginRequest loginRequest, HttpSession session) {
		  Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
		    boolean isAuthenticated = isAuthenticated(authentication);
		    if (isAuthenticated) {
		        SecurityContextHolder.getContext().setAuthentication(authentication);
		    }
		    User user = userRepository.findByEmail(loginRequest.getEmail());
		    session.setAttribute("user", user);
			return new LoginResponse(user.getId(), session.getId());
	}
	
	private boolean isAuthenticated(Authentication authentication) {
	    return authentication != null && !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public User update(@RequestBody User user) {
		return userRepository.save(user);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id")Long id) {
		userRepository.delete(id);
	}
	
}
