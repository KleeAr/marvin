package ar.com.klee.marvin.app;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;

public class EmailMain {

	public static void main(String[] args) throws Exception {
		HtmlEmail email = new HtmlEmail();
		email.setHostName("smtp.gmail.com");
		email.setSmtpPort(587);
		email.setAuthenticator(new DefaultAuthenticator("equipomarvin@gmail.com", "unlam2015"));
		email.setStartTLSEnabled(true);
		email.setFrom("equipomarvin@gmail.com");
		email.setSubject("Recuperaci�n de contrase�a");
		email.setHtmlMsg("<html><head></head><body><a href=\"http://marvin.kleear.com.ar/token/" + "xBt3fkl" + "\">Click para recuperar contrase�a</a></body></html>");
		email.addTo("matias.d.salerno@gmail.com");
		email.send();
		System.out.println("email sent");
	}

}
