package ar.com.klee.marvin.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import ar.com.klee.marvin.model.PasswordToken;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class PasswordTokenExpiredException extends RuntimeException {

	public PasswordTokenExpiredException(PasswordToken token) {
		super("Token " + token.getCode() + " is expired. Request a new token");
	}

	public PasswordTokenExpiredException(String token) {
		super("Token " + token + " not found");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
