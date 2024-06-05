package cm.xenonit.gelodia.openerpmailsender.notification.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * @author bamk
 * @version 1.0
 * @since 12/02/2024
 */
@ResponseStatus(INTERNAL_SERVER_ERROR)
public class MailInternalServerErrorException extends RuntimeException {
    public MailInternalServerErrorException(String message) {
        super(message);
    }
}
