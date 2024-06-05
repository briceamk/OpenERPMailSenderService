package cm.xenonit.gelodia.openerpmailsender.security.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
@ResponseStatus(NOT_FOUND)
public class VerificationNotFoundException extends RuntimeException {
    public VerificationNotFoundException(String message) {
        super(message);
    }
}
