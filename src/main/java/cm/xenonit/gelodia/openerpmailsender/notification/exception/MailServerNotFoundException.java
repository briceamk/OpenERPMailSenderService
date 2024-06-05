package cm.xenonit.gelodia.openerpmailsender.notification.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @author bamk
 * @version 1.0
 * @since 24/01/2024
 */
@ResponseStatus(NOT_FOUND)
public class MailServerNotFoundException extends RuntimeException {
    public MailServerNotFoundException(String message) {
        super(message);
    }
}
