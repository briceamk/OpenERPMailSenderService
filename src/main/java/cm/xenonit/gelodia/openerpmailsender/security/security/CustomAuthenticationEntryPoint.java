package cm.xenonit.gelodia.openerpmailsender.security.security;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */

import cm.xenonit.gelodia.openerpmailsender.common.dto.ApiErrorResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.builder()
                .timestamp(now().toString())
                .success(false)
                .code(UNAUTHORIZED.value())
                .status(UNAUTHORIZED.name())
                .reason("You need to login to access to this resource.")
                .build();

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(UNAUTHORIZED.value());
        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, apiErrorResponseDto);
        out.flush();
    }
}
