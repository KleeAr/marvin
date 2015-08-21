package ar.com.klee.marvin.service;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import ar.com.klee.marvin.model.User;
import ar.com.klee.marvin.repository.UserRepository;

@Service
public class MarvinUserDetailsService implements UserDetailsService {

	private UserRepository userRepository;
	
	@Autowired
	public MarvinUserDetailsService(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		return new org.springframework.security.core.userdetails.User(username, user.getPassword(), getAuthorities());
	}

	private Collection<? extends GrantedAuthority> getAuthorities() {
		return Arrays.asList(() -> "ROLE_BASIC");
	}

}
