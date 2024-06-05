package cm.xenonit.gelodia.openerpmailsender.openerp.service.implementation;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.Mail;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailServer;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailState;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailTemplateType;
import cm.xenonit.gelodia.openerpmailsender.openerp.MailInstanceRepository;
import cm.xenonit.gelodia.openerpmailsender.openerp.domain.Instance;
import cm.xenonit.gelodia.openerpmailsender.openerp.service.MailInstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author bamk
 * @version 1.0
 * @since 24/02/2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailInstanceServiceImplementation implements MailInstanceService {

    private final MailInstanceRepository mailInstanceRepository;

    @Override
    public List<Integer> fetchMailExternalIdsInQueueByInstanceAndTemplate(Instance instance, MailTemplateType type) {
        return mailInstanceRepository.findByInstanceAndType(instance, type).stream()
                .filter(mail -> mail.getExternalId() != null)
                .map(mail -> Integer.valueOf(String.valueOf(mail.getExternalId())))
                .toList();
    }

    @Override
    public List<Mail> findMailByStateAndMailServerAndInstance(MailState mailState, MailServer mailServer, Instance instance) {
        return mailInstanceRepository.findByStateAndMailServerAndInstance(mailState, mailServer, instance);
    }
}
