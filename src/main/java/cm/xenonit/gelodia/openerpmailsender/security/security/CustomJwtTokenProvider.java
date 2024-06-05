package cm.xenonit.gelodia.openerpmailsender.security.security;

import cm.xenonit.gelodia.openerpmailsender.security.exception.UserNotFoundException;
import cm.xenonit.gelodia.openerpmailsender.security.exception.UserUnauthorizedException;
import cm.xenonit.gelodia.openerpmailsender.security.repository.UserRepository;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static cm.xenonit.gelodia.openerpmailsender.security.constant.SecurityConstant.*;
import static com.auth0.jwt.JWT.create;
import static com.auth0.jwt.JWT.require;
import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomJwtTokenProvider {


    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.token.expiration.time}")
    private int accessTokenExpirationTime;

    @Value("${jwt.refresh.token.expiration.time}")
    private int refreshTokenExpirationTime;

    public String createJwtAccessToken(CustomUserDetails userDetails) {
        String[] claims = getClaimsFromUserDetails(userDetails);
        Date issuedAt = new Date();
        return create().withIssuer(XENON_BYTE_SARL)
                .withAudience(OPENERP_EMAIL_SENDER)
                .withSubject(userDetails.getUser().getId())
                .withArrayClaim(AUTHORITIES, claims)
                .withIssuedAt(issuedAt)
                .withExpiresAt(new Date(issuedAt.toInstant().toEpochMilli() + accessTokenExpirationTime))
                .sign(HMAC512(secret.getBytes()));
    }

    public String createJwtRefreshToken(CustomUserDetails userDetails) {
        Date issuedAt = new Date();
        return create().withIssuer(XENON_BYTE_SARL)
                .withAudience(OPENERP_EMAIL_SENDER)
                .withSubject(userDetails.getUser().getId())
                .withIssuedAt(issuedAt)
                .withExpiresAt(new Date(issuedAt.toInstant().toEpochMilli() + refreshTokenExpirationTime))
                .sign(HMAC512(secret.getBytes()));
    }

    private String[] getClaimsFromUserDetails(CustomUserDetails userDetails) {
        return userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);
    }

    public String getSubject(String token, HttpServletRequest request) {
        try {
            return getJWTVerifier().verify(token).getSubject();
        } catch (TokenExpiredException exception) {
            log.error(exception.getMessage());
            request.setAttribute("expiredMessage", exception.getMessage());
            throw new UserUnauthorizedException(exception.getMessage());
        } catch (InvalidClaimException exception) {
            log.error(exception.getMessage());
            request.setAttribute("invalidClaim", exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }

    private JWTVerifier getJWTVerifier() {
        try {
            Algorithm algorithm = HMAC512(secret);
            return require(algorithm).withIssuer(XENON_BYTE_SARL).build();
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Token can't be verified.");
        }
    }

    public boolean isValidToken(String token, HttpServletRequest request) {
        return token != null && !token.isEmpty() && isExpiredToken(getJWTVerifier(), token, request);
    }

    private boolean isExpiredToken(JWTVerifier verifier, String token, HttpServletRequest request) {
        try {
            return verifier.verify(token).getExpiresAt().after(new Date());
        } catch (TokenExpiredException exception) {
            log.error(exception.getMessage());
            request.setAttribute("expiredMessage", exception.getMessage());
            throw new UserUnauthorizedException(exception.getMessage());
        } catch (InvalidClaimException exception) {
            log.error(exception.getMessage());
            request.setAttribute("invalidClaim", exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw exception;
        }

    }

    public List<GrantedAuthority> getAuthorities(String token) {
        return stream(getClaimsFromToken(token)).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    private String[] getClaimsFromToken(String token) {
        return getJWTVerifier().verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    public Authentication getAuthentication(String id, List<GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userRepository.findById(id).orElseThrow(() ->
                                new UserNotFoundException("User with id not found. Please try again.")),
              null,
                        authorities
                );
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken;
    }

    public boolean isValidSubject(String subject) {
        return StringUtils.isNoneEmpty(subject);
    }

    public boolean isHeaderTokenValid(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION) != null
                && request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX)
                && this.isValidToken(request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length()), request);
    }
}
