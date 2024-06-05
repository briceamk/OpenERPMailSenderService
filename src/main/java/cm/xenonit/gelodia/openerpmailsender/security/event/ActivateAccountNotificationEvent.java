package cm.xenonit.gelodia.openerpmailsender.security.event;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailTemplateType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * @author bamk
 * @version 1.0
 * @since 14/02/2024
 */
@Getter
@Setter
public class ActivateAccountNotificationEvent extends ApplicationEvent {
    private String email;
    private String fullName;
    private String verificationUrl;
    private String expireIn;
    private MailTemplateType type;
    private String subject;

    public ActivateAccountNotificationEvent(String email, String fullName, String verificationUrl, String expireIn, MailTemplateType type, String subject) {
        super(email);
        this.email = email;
        this.fullName = fullName;
        this.verificationUrl = verificationUrl;
        this.expireIn = expireIn;
        this.type = type;
        this.subject = subject;
    }
}
