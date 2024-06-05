package cm.xenonit.gelodia.openerpmailsender.common.exception;

import cm.xenonit.gelodia.openerpmailsender.common.dto.ApiErrorResponseDto;
import cm.xenonit.gelodia.openerpmailsender.common.dto.ValidationErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @author bamk
 * @version 1.0
 * @since 24/01/2024
 */
@Slf4j
public abstract class CommonResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    protected static final String PATH_URI_REPLACE = "uri=";
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<ValidationErrorDto> errors = new ArrayList<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(fieldError -> errors.add(
                        ValidationErrorDto.builder()
                                .field(fieldError.getField())
                                .message(fieldError.getDefaultMessage())
                                .build()
                        ));
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
                                .error(errors)
                                .build()
                );
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception,
            Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {
        log.error("", exception);
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ApiErrorResponseDto.builder()
                                .status(BAD_REQUEST.name())
                                .code(BAD_REQUEST.value())
                                .success(false)
                                .reason(exception.getMessage())
                                .developerMessage(exception.getMessage())
                                .timestamp(LocalTime.now().toString())
                                .build()
                );
    }
}
