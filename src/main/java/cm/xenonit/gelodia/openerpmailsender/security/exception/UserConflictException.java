package cm.xenonit.gelodia.openerpmailsender.security.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
@ResponseStatus(CONFLICT)
public class UserConflictException extends RuntimeException {
    public UserConflictException(String message) {
        super(message);
    }
}
