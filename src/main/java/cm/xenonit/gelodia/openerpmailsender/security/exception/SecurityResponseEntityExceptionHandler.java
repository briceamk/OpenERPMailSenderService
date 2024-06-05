package cm.xenonit.gelodia.openerpmailsender.security.exception;


import cm.xenonit.gelodia.openerpmailsender.common.dto.ApiErrorResponseDto;
import cm.xenonit.gelodia.openerpmailsender.common.exception.CommonResponseEntityExceptionHandler;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
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
public class SecurityResponseEntityExceptionHandler extends CommonResponseEntityExceptionHandler {

    @ExceptionHandler({
            UserBadRequestException.class,
            VerificationBadRequestException.class,
            JWTVerificationException.class,
            InvalidClaimException.class,
    })
    protected final ResponseEntity<ApiErrorResponseDto> handleUserBadRequestException
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

    @ExceptionHandler({RoleNotFoundException.class, UserNotFoundException.class, VerificationNotFoundException.class})
    protected final ResponseEntity<ApiErrorResponseDto> handleRoleNotFoundException
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

    @ExceptionHandler({UserConflictException.class})
    protected final ResponseEntity<ApiErrorResponseDto> handleUserConflictException
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

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<ApiErrorResponseDto> handleBadCredentialsException(
            BadCredentialsException exception, WebRequest request) {
        log.error("", exception);
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ApiErrorResponseDto.builder()
                                .status(BAD_REQUEST.name())
                                .code(BAD_REQUEST.value())
                                .success(false)
                                .reason(exception.getMessage() + ", Incorrect email or password")
                                .developerMessage(exception.getMessage())
                                .timestamp(LocalTime.now().toString())
                                .path(request.getDescription(false).replaceAll(PATH_URI_REPLACE, ""))
                                .build()
                );
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ApiErrorResponseDto> handleAccessDeniedException(
            AccessDeniedException exception, WebRequest request) {
        log.error("", exception);
        return ResponseEntity
                .status(FORBIDDEN)
                .body(
                        ApiErrorResponseDto.builder()
                                .status(FORBIDDEN.name())
                                .code(FORBIDDEN.value())
                                .success(false)
                                .reason("Access denied. You don't have permission to access this resource")
                                .developerMessage(exception.getMessage())
                                .timestamp(LocalTime.now().toString())
                                .path(request.getDescription(false).replaceAll(PATH_URI_REPLACE, ""))
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiErrorResponseDto> handleException(
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

    @ExceptionHandler(DisabledException.class)
    protected ResponseEntity<ApiErrorResponseDto> handleDisabledException(
            DisabledException exception, WebRequest request) {
        log.error("", exception);
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ApiErrorResponseDto.builder()
                                .status(BAD_REQUEST.name())
                                .code(BAD_REQUEST.value())
                                .success(false)
                                .reason("User account is currently disabled.")
                                .developerMessage(exception.getMessage())
                                .timestamp(LocalTime.now().toString())
                                .path(request.getDescription(false).replaceAll(PATH_URI_REPLACE, ""))
                                .build()
                );
    }

    @ExceptionHandler(LockedException.class)
    protected ResponseEntity<ApiErrorResponseDto> handleLockedException(
            LockedException exception, WebRequest request) {
        log.error("", exception);
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ApiErrorResponseDto.builder()
                                .status(BAD_REQUEST.name())
                                .code(BAD_REQUEST.value())
                                .success(false)
                                .reason("Your account is locked.")
                                .developerMessage(exception.getMessage())
                                .timestamp(LocalTime.now().toString())
                                .path(request.getDescription(false).replaceAll(PATH_URI_REPLACE, ""))
                                .build()
                );
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    protected ResponseEntity<ApiErrorResponseDto> handleCredentialsExpiredException(
            CredentialsExpiredException exception, WebRequest request) {
        log.error("", exception);
        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ApiErrorResponseDto.builder()
                                .status(BAD_REQUEST.name())
                                .code(BAD_REQUEST.value())
                                .success(false)
                                .reason("Your account is expired. Request a link to reset your password.")
                                .developerMessage(exception.getMessage())
                                .timestamp(LocalTime.now().toString())
                                .path(request.getDescription(false).replaceAll(PATH_URI_REPLACE, ""))
                                .build()
                );
    }

    @ExceptionHandler({UserUnauthorizedException.class})
    protected ResponseEntity<ApiErrorResponseDto> handleUserUnauthorizedException(
            UserUnauthorizedException exception, WebRequest request) {
        log.error("", exception);
        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ApiErrorResponseDto.builder()
                                .status(UNAUTHORIZED.name())
                                .code(UNAUTHORIZED.value())
                                .success(false)
                                .reason(exception.getMessage())
                                .developerMessage(exception.getMessage())
                                .timestamp(LocalTime.now().toString())
                                .path(request.getDescription(false).replaceAll(PATH_URI_REPLACE, ""))
                                .build()
                );
    }
}
