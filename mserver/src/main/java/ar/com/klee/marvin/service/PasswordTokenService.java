package ar.com.klee.marvin.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.com.klee.marvin.model.PasswordToken;
import ar.com.klee.marvin.model.User;
import ar.com.klee.marvin.repository.PasswordTokenRepository;
import ar.com.klee.marvin.repository.UserRepository;
import ar.com.klee.marvin.representation.ResetPasswordRequest;

@Service
public class PasswordTokenService {

	private static final int DURATION = 60*5*1000;
	private PasswordTokenRepository tokenRepository;
	private UserRepository	userRepository;
	
	@Autowired
	public PasswordTokenService(PasswordTokenRepository repository, UserRepository	userRepository) {
		this.tokenRepository = repository;
		this.userRepository = userRepository;
	}
	
	public void generatePasswordTokenAndSendRecoveryEmail(final User user) {
		try {
			tokenRepository.deleteByUserId(user.getId());
			PasswordToken token = new PasswordToken(user.getId(), DURATION);
			HtmlEmail email = new HtmlEmail();
			email.setHostName("smtp.gmail.com");
			email.setSmtpPort(465);
			email.setAuthenticator(new DefaultAuthenticator("equipomarvin@gmail.com", "unlam2015"));
			email.setSSLOnConnect(true);
			email.setFrom("equipomarvin@gmail.com");
			email.setSubject("Recuperación de contraseña");
			email.setHtmlMsg("<html><head></head><body><a href=\"http://marvin.kleear.com.ar/token/" + token.getCode() + "\">Click para recuperar contraseña</a></body></html>");
			email.addTo(user.getEmail());
			email.send();
			tokenRepository.save(token);
		} catch (EmailException e) {
			throw new RuntimeException("Error while sending password recovery email", e);
		}
	}

	public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
		PasswordToken token = tokenRepository.findOne(resetPasswordRequest.getToken());
		if (token == null) {
			throw new PasswordTokenExpiredException(resetPasswordRequest.getToken());
		}
		User user = userRepository.findOne(token.getUserId());
		if(token.isExpired()) {
			throw new PasswordTokenExpiredException(token);
		}
		user.setPassword(resetPasswordRequest.getPassword());
		userRepository.save(user);
		tokenRepository.deleteByUserId(user.getId());
	}
}
