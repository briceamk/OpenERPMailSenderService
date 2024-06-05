package cm.xenonit.gelodia.openerpmailsender.security.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * @author bamk
 * @version 1.0
 * @since 11/02/2024
 */
@ResponseStatus(UNAUTHORIZED)
public class UserUnauthorizedException extends RuntimeException {
    public UserUnauthorizedException(String message) {
        super(message);
    }
}
