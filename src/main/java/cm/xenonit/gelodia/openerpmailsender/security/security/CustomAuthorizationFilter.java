package cm.xenonit.gelodia.openerpmailsender.security.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static cm.xenonit.gelodia.openerpmailsender.security.constant.SecurityConstant.*;
import static cm.xenonit.gelodia.openerpmailsender.security.util.ExceptionUtil.processError;
import static java.util.Arrays.asList;
import static java.util.Map.of;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final CustomJwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            Map<String, String> requestValues = getRequestValues(request);
            String token = getToken(request);
            String subject = requestValues.get(USER_ID_KEY);
            if(tokenProvider.isValidToken(token, request ) && tokenProvider.isValidSubject(subject)) {
                List<GrantedAuthority> authorities = tokenProvider.getAuthorities(requestValues.get(TOKEN));
                Authentication authentication = tokenProvider.getAuthentication(subject, authorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            processError(request, response, exception);
            //throw new UserUnauthorizedException(exception.getMessage());
        }
    }

    private Map<String, String> getRequestValues(HttpServletRequest request) {
        return of(USER_ID_KEY, tokenProvider.getSubject(getToken(request), request), TOKEN, getToken(request));
    }

    private String getToken(HttpServletRequest request) {
        tokenProvider.isHeaderTokenValid(request);
        return  ofNullable(request.getHeader(AUTHORIZATION))
                .filter(header -> header.startsWith(TOKEN_PREFIX))
                .map(token -> token.replace(TOKEN_PREFIX, EMPTY)).get();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getHeader(AUTHORIZATION) == null || !request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX) ||
                request.getMethod().equalsIgnoreCase(HTTP_OPTIONS_METHOD_TO_NOT_FILTER) || asList(PUBLIC_URLS_TO_NOT_FILTER).contains(request.getRequestURI());
    }
}
