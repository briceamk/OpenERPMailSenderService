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
public class SendMfaNotificationEvent extends ApplicationEvent {

    private String email;
    private String fullName;
    private String code;
    private String expireIn;
    private MailTemplateType type;
    private String subject;

    public SendMfaNotificationEvent(String email, String fullName, String code, String expireIn, MailTemplateType type, String subject) {
        super(email);
        this.email = email;
        this.fullName = fullName;
        this.code = code;
        this.expireIn = expireIn;
        this.type = type;
        this.subject = subject;
    }
}
