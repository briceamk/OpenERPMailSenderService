package cm.xenonit.gelodia.openerpmailsender.notification.event;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailTemplateType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

/**
 * @author bamk
 * @version 1.0
 * @since 14/02/2024
 */
@Slf4j
@Getter
@Setter
public class VerifyMailServerNotificationEvent extends ApplicationEvent {
    private String email;
    private String subject;
    private MailTemplateType type;
    private String owner;
    private String code;

    public VerifyMailServerNotificationEvent(String email, String subject, MailTemplateType type, String owner, String code) {
        super(email);
        this.email = email;
        this.subject = subject;
        this.type = type;
        this.owner = owner;
        this.code = code;
    }
}
