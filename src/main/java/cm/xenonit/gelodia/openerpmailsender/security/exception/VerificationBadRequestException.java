package cm.xenonit.gelodia.openerpmailsender.security.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
@ResponseStatus(BAD_REQUEST)
public class VerificationBadRequestException extends RuntimeException {
    public VerificationBadRequestException(String message) {
        super(message);
    }
}