package cm.xenonit.gelodia.openerpmailsender.notification.exception;

import cm.xenonit.gelodia.openerpmailsender.common.dto.ApiErrorResponseDto;
import cm.xenonit.gelodia.openerpmailsender.common.exception.CommonResponseEntityExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

/**
 * @author bamk
 * @version 1.0
 * @since 24/01/2024
 */
@Slf4j
@RestControllerAdvice
public class NotificationResponseEntityExceptionHandler extends CommonResponseEntityExceptionHandler {

    @ExceptionHandler({MailServerBadRequestException.class, MailServerBadRequestException.class})
    protected final ResponseEntity<ApiErrorResponseDto> handleEmailServerBadRequestException
            (RuntimeException exception, WebRequest request) {
        log.error("", exception);
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ApiErrorResponseDto.builder()
                                .timestamp(now().toString())
                                .code(BAD_REQUEST.value())
                                .status(BAD_REQUEST.getReasonPhrase())
                                .success(false)
                                .reason(exception.getLocalizedMessage())
                                .path(request.getDescription(false).replaceAll(PATH_URI_REPLACE, ""))
                                .build()
                );
    }

    @ExceptionHandler({MailServerNotFoundException.class, MailTemplateNotFoundException.class, MailNotFoundException.class})
    protected final ResponseEntity<ApiErrorResponseDto> handleEmailServerNotFoundException
            (RuntimeException exception, WebRequest request) {
        log.error("", exception);
        return ResponseEntity
                .status(NOT_FOUND)
                .body(
                        ApiErrorResponseDto.builder()
                                .timestamp(now().toString())
                                .code(NOT_FOUND.value())
                                .status(NOT_FOUND.getReasonPhrase())
                                .success(false)
                                .reason(exception.getLocalizedMessage())
                                .path(request.getDescription(false).replaceAll(PATH_URI_REPLACE, ""))
                                .build()
                );
    }

    @ExceptionHandler({MailServerConflictException.class})
    protected final ResponseEntity<ApiErrorResponseDto> handleEmailServerConflictException
            (RuntimeException exception, WebRequest request) {
        log.error("", exception);
        return ResponseEntity
                .status(CONFLICT)
                .body(
                        ApiErrorResponseDto.builder()
                                .timestamp(now().toString())
                                .code(CONFLICT.value())
                                .status(CONFLICT.getReasonPhrase())
                                .success(false)
                                .reason(exception.getLocalizedMessage())
                                .path(request.getDescription(false).replaceAll(PATH_URI_REPLACE, ""))
                                .build()
                );
    }

    @ExceptionHandler({MailInternalServerErrorException.class})
    protected final ResponseEntity<ApiErrorResponseDto> handleMailInternalServerErrorException
            (RuntimeException exception) {
        log.error("", exception);
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ApiErrorResponseDto.builder()
                                .timestamp(now().toString())
                                .code(INTERNAL_SERVER_ERROR.value())
                                .status(INTERNAL_SERVER_ERROR.getReasonPhrase())
                                .success(false)
                                .reason(exception.getLocalizedMessage())
                                .build()
                );
    }
}
