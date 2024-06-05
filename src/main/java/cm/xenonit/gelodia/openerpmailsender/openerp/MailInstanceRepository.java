package cm.xenonit.gelodia.openerpmailsender.openerp;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.Mail;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailServer;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailState;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailTemplateType;
import cm.xenonit.gelodia.openerpmailsender.openerp.domain.Instance;
import cm.xenonit.gelodia.openerpmailsender.openerp.domain.MailInstance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author bamk
 * @version 1.0
 * @since 24/02/2024
 */
public interface MailInstanceRepository extends JpaRepository<MailInstance, String> {
    List<MailInstance> findByInstanceAndType(Instance instance, MailTemplateType type);

    List<Mail> findByStateAndMailServerAndInstance(MailState mailState, MailServer mailServer, Instance instance);
}
