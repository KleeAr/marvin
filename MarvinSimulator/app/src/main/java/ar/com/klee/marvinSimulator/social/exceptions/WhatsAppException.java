package ar.com.klee.marvinSimulator.social.exceptions;

/**
 * Class that represents any exception thrown by the {@link ar.com.klee.social.services.WhatsAppService}
 *
 * @author msalerno
 */
public class WhatsAppException extends RuntimeException {

    public WhatsAppException(String message, Throwable cause) {
        super(message, cause);
    }
}
