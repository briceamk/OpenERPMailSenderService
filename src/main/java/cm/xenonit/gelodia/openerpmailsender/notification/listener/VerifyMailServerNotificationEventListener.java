package cm.xenonit.gelodia.openerpmailsender.notification.listener;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.Mail;
import cm.xenonit.gelodia.openerpmailsender.notification.event.VerifyMailServerNotificationEvent;
import cm.xenonit.gelodia.openerpmailsender.notification.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Map.of;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

/**
 * @author bamk
 * @version 1.0
 * @since 14/02/2024
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VerifyMailServerNotificationEventListener {

    private final MailService mailService;

    @EventListener
    @Transactional(propagation = REQUIRED)
    public void onVerifyMailServerNotificationEvent(VerifyMailServerNotificationEvent event) {
        log.info("Publishing new event for to verify mail server");
        mailService.createEmail(
                Mail.builder()
                        .to(event.getEmail())
                        .subject(event.getSubject())
                        .type(event.getType())
                        .attributes(of("owner", event.getOwner(), "code", event.getCode()))
                        .build()
        );

    }
}
