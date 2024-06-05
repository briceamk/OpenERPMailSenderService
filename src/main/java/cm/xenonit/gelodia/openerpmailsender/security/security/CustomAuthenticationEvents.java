package cm.xenonit.gelodia.openerpmailsender.security.security;

import cm.xenonit.gelodia.openerpmailsender.security.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class CustomAuthenticationEvents {

    private final UserService userService;

    @EventListener
    @Transactional(propagation = REQUIRES_NEW)
    public void onSuccess(AuthenticationSuccessEvent successEvent) {
        String email = successEvent.getAuthentication().getName();
        log.info("Login successful for user {}", email);
        userService.resetBruteForceCounter(email);
    }

    @EventListener
    @Transactional(propagation = REQUIRES_NEW)
    public void onFailure(AbstractAuthenticationFailureEvent failureEvent) {
        String email = failureEvent.getAuthentication().getName();
        log.info("Login failed for user {}", email);
        userService.registerLoginFailure(email);
    }
}
