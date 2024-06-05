package cm.xenonit.gelodia.openerpmailsender.openerp.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * @author bamk
 * @version 1.0
 * @since 21/02/2024
 */
@ResponseStatus(CONFLICT)
public class InstanceConflictException extends RuntimeException {
    public InstanceConflictException(String message) {
        super(message);
    }
}
