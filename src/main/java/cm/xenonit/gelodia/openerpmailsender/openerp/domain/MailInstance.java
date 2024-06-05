package cm.xenonit.gelodia.openerpmailsender.openerp.domain;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.Mail;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailServer;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailTemplate;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailState;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailTemplateType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author bamk
 * @version 1.0
 * @since 24/02/2024
 */
@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("mail_instance")
public class MailInstance extends Mail {
    @ManyToOne
    @JoinColumn(name = "c_instance_id")
    private Instance instance;
}
