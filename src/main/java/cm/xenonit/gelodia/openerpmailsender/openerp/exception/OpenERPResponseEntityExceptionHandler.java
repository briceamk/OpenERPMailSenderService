package cm.xenonit.gelodia.openerpmailsender.openerp.exception;


import cm.xenonit.gelodia.openerpmailsender.common.dto.ApiErrorResponseDto;
import cm.xenonit.gelodia.openerpmailsender.common.exception.CommonResponseEntityExceptionHandler;
import cm.xenonit.gelodia.openerpmailsender.schedule.exception.JobInfoBadRequestException;
import cm.xenonit.gelodia.openerpmailsender.schedule.exception.JobInfoConflictException;
import cm.xenonit.gelodia.openerpmailsender.schedule.exception.JobInfoNotFoundException;
import cm.xenonit.gelodia.openerpmailsender.schedule.exception.JobInfoTechnicalException;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlrpc.XmlRpcException;
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
public class OpenERPResponseEntityExceptionHandler extends CommonResponseEntityExceptionHandler {

    @ExceptionHandler({
            InstanceBadRequestException.class,
    })
    protected final ResponseEntity<ApiErrorResponseDto> handleBadRequestException
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

    @ExceptionHandler({InstanceNotFoundException.class})
    protected final ResponseEntity<ApiErrorResponseDto> handleNotFoundException
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

    @ExceptionHandler({InstanceConflictException.class})
    protected final ResponseEntity<ApiErrorResponseDto> handleConflictException
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

    @ExceptionHandler({XmlRpcException.class})
    protected final ResponseEntity<ApiErrorResponseDto> handleConflictException
            (Exception exception, WebRequest request) {
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
                                .developerMessage(exception.getMessage())
                                .path(request.getDescription(false).replaceAll(PATH_URI_REPLACE, ""))
                                .build()
                );
    }


}
