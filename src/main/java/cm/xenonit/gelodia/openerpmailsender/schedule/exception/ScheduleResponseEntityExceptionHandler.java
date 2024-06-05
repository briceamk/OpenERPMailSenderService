package cm.xenonit.gelodia.openerpmailsender.schedule.exception;


import cm.xenonit.gelodia.openerpmailsender.common.dto.ApiErrorResponseDto;
import cm.xenonit.gelodia.openerpmailsender.common.exception.CommonResponseEntityExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalTime;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

/**
 * @author bamk
 * @version 1.0
 * @since 24/01/2024
 */
@Slf4j
@RestControllerAdvice
public class ScheduleResponseEntityExceptionHandler extends CommonResponseEntityExceptionHandler {

    @ExceptionHandler({
            JobInfoBadRequestException.class,
    })
    protected final ResponseEntity<ApiErrorResponseDto> handleJobInfoBadRequestException
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

    @ExceptionHandler({JobInfoNotFoundException.class})
    protected final ResponseEntity<ApiErrorResponseDto> handleJobInfoNotFoundException
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

    @ExceptionHandler({JobInfoConflictException.class})
    protected final ResponseEntity<ApiErrorResponseDto> handleJobInfoConflictException
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

    @ExceptionHandler(JobInfoTechnicalException.class)
    protected ResponseEntity<ApiErrorResponseDto> handleJobInfoTechnicalException(
            Exception exception) {
        log.error("", exception);
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(
                        ApiErrorResponseDto.builder()
                                .status(INTERNAL_SERVER_ERROR.name())
                                .code(INTERNAL_SERVER_ERROR.value())
                                .success(false)
                                .reason(exception.getMessage())
                                .developerMessage(exception.getMessage())
                                .timestamp(LocalTime.now().toString())
                                .build()
                );
    }
}
