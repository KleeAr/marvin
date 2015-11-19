package ar.com.klee.marvin.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class WrongPasswordException extends RuntimeException {

	public WrongPasswordException(String message) {
		super(message);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -1522937723038072295L;

	
	
}
