package cm.xenonit.gelodia.openerpmailsender.security.util;

import cm.xenonit.gelodia.openerpmailsender.common.dto.ApiErrorResponseDto;
import cm.xenonit.gelodia.openerpmailsender.security.exception.*;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import java.io.OutputStream;

import static java.time.LocalTime.now;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author bamk
 * @version 1.0
 * @since 26/01/2024
 */
@Slf4j
public class ExceptionUtil {

    public static void processError(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        if(exception instanceof DisabledException || exception instanceof BadCredentialsException
               || exception instanceof LockedException || exception instanceof InvalidClaimException || exception instanceof RoleNotFoundException
        || exception instanceof UserBadRequestException || exception instanceof UserConflictException || exception instanceof UserNotFoundException
                || exception instanceof VerificationBadRequestException ||
        exception instanceof VerificationNotFoundException) {
            ApiErrorResponseDto apiErrorResponseDto = getHttpResponse(request, response, exception.getMessage(), BAD_REQUEST);
            writeResponse(response, apiErrorResponseDto);
        } else if (exception instanceof TokenExpiredException) {
            ApiErrorResponseDto apiErrorResponseDto = getHttpResponse(request, response, exception.getMessage(), UNAUTHORIZED);
            writeResponse(response, apiErrorResponseDto);
        } else if (exception instanceof UserUnauthorizedException) {
            ApiErrorResponseDto apiErrorResponseDto = getHttpResponse(request, response, exception.getMessage(), UNAUTHORIZED);
            writeResponse(response, apiErrorResponseDto);
        } else {
            ApiErrorResponseDto apiErrorResponseDto = getHttpResponse(request, response, "An error occurred. Please try again", INTERNAL_SERVER_ERROR);
            writeResponse(response, apiErrorResponseDto);
        }
        log.error("", exception);
    }

    private static ApiErrorResponseDto getHttpResponse(HttpServletRequest request, HttpServletResponse response, String message, HttpStatus httpStatus)  {
        ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.builder()
                .timestamp(now().toString())
                .reason(message)
                .developerMessage(message)
                .code(httpStatus.value())
                .status(httpStatus.name())
                .path(request.getRequestURI())
                .success(false)
                .build();
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.value());
        return apiErrorResponseDto;
    }

    private static void writeResponse(HttpServletResponse response, ApiErrorResponseDto apiErrorResponseDto) {
        OutputStream out;
        try {
            out = response.getOutputStream();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(out, apiErrorResponseDto);
            out.flush();
        } catch (Exception exception) {
            log.error("", exception);
            exception.printStackTrace();
        }
    }
}
