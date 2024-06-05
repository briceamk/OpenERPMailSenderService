package cm.xenonit.gelodia.openerpmailsender.schedule.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.CONFLICT;

/**
 * @author bamk
 * @version 1.0
 * @since 17/02/2024
 */
@ResponseStatus(CONFLICT)
public class JobInfoConflictException extends RuntimeException {
    public JobInfoConflictException(String message) {
        super(message);
    }
}
