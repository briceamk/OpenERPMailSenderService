package cm.xenonit.gelodia.openerpmailsender.notification.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @author bamk
 * @version 1.0
 * @since 11/02/2024
 */
@ResponseStatus(BAD_REQUEST)
public class MailSenderBadException extends RuntimeException {
    public MailSenderBadException(String message) {
        super(message);
    }
}
