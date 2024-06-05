package cm.xenonit.gelodia.openerpmailsender.openerp.service;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.Mail;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailServer;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailState;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailTemplateType;
import cm.xenonit.gelodia.openerpmailsender.openerp.domain.Instance;

import java.util.List;

/**
 * @author bamk
 * @version 1.0
 * @since 24/02/2024
 */
public interface MailInstanceService {
    List<Integer> fetchMailExternalIdsInQueueByInstanceAndTemplate(Instance instance, MailTemplateType type);

    List<Mail> findMailByStateAndMailServerAndInstance(MailState mailState, MailServer mailServer, Instance instance);
}
