package cm.xenonit.gelodia.openerpmailsender.schedule.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * @author bamk
 * @version 1.0
 * @since 17/02/2024
 */
@ResponseStatus(INTERNAL_SERVER_ERROR)
public class JobInfoTechnicalException extends RuntimeException {
    public JobInfoTechnicalException(String message) {
        super(message);
    }
}
