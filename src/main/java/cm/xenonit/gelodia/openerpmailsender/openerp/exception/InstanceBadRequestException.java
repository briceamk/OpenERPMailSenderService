package cm.xenonit.gelodia.openerpmailsender.openerp.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @author bamk
 * @version 1.0
 * @since 25/02/2024
 */
@ResponseStatus(BAD_REQUEST)
public class InstanceBadRequestException extends RuntimeException {
    public InstanceBadRequestException(String message) {
        super(message);
    }
}
