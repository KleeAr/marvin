package ar.com.klee.marvin.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ar.com.klee.marvin.model.User;

@RestController
public class UserController {

	@RequestMapping(method = RequestMethod.GET,value = "/users")
	public User getUsers() {
		return new User(1L, "Matías", "Salerno", "matias.d.salerno@gmail.com");
	}
}
