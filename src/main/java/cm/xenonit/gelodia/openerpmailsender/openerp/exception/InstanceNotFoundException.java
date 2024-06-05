package cm.xenonit.gelodia.openerpmailsender.openerp.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @author bamk
 * @version 1.0
 * @since 21/02/2024
 */
@ResponseStatus(NOT_FOUND)
public class InstanceNotFoundException extends RuntimeException {
    public InstanceNotFoundException(String message) {
        super(message);
    }
}
