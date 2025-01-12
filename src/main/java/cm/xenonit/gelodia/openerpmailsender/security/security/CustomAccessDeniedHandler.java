package cm.xenonit.gelodia.openerpmailsender.security.security;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */

import cm.xenonit.gelodia.openerpmailsender.common.dto.ApiErrorResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.builder()
                .timestamp(now().toString())
                .success(false)
                .code(FORBIDDEN.value())
                .status(FORBIDDEN.name())
                .reason("You don't have enough permission to access to this resource.")
                .build();

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());
        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, apiErrorResponseDto);
        out.flush();

    }
}
