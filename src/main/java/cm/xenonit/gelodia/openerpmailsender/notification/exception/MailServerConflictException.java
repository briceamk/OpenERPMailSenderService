package cm.xenonit.gelodia.openerpmailsender.notification.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author bamk
 * @version 1.0
 * @since 24/01/2024
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class MailServerConflictException extends RuntimeException {
    public MailServerConflictException(String message) {
        super(message);
    }
}
