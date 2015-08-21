package ar.com.klee.marvin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ar.com.klee.marvin.model.User;
import ar.com.klee.marvin.repository.UserRepository;

@RestController
@RequestMapping(value = "/users")
public class UserController {

	private UserRepository userRepository;
	
	@Autowired	
	public UserController(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

	@RequestMapping(method = RequestMethod.GET)
	public Iterable<User> getAll() {
		return userRepository.findAll();
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public User getById(@PathVariable("id")Long id) {
		return userRepository.findOne(id);
	}
	
	@RequestMapping(value ="register", method = RequestMethod.POST)
	public User save(@RequestBody User user) {
		return userRepository.save(user);
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
