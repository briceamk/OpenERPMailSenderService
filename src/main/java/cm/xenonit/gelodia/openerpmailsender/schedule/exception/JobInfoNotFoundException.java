package cm.xenonit.gelodia.openerpmailsender.schedule.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * @author bamk
 * @version 1.0
 * @since 17/02/2024
 */
@ResponseStatus(NOT_FOUND)
public class JobInfoNotFoundException extends RuntimeException {
    public JobInfoNotFoundException(String message) {
        super(message);
    }
}
