package cm.xenonit.gelodia.openerpmailsender.schedule.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @author bamk
 * @version 1.0
 * @since 17/02/2024
 */
@ResponseStatus(BAD_REQUEST)
public class JobInfoBadRequestException extends RuntimeException {
    public JobInfoBadRequestException(String message) {
        super(message);
    }
}
