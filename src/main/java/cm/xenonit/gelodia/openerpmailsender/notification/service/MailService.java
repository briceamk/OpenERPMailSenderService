package cm.xenonit.gelodia.openerpmailsender.notification.service;

import cm.xenonit.gelodia.openerpmailsender.common.enums.SortDirection;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.Mail;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailServer;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailState;
import cm.xenonit.gelodia.openerpmailsender.notification.resource.dto.MailDto;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author bamk
 * @version 1.0
 * @since 11/02/2024
 */
public interface MailService {

    Mail createEmail(Mail mail);

    void sendMails();

    Mail findById(String id);

    List<Mail> findMailWithExternalIdToComplete();

    boolean mailExistByExternalId(Long remoteMailId);

    Mail fetchMailById(String id);

    Page<Mail> fetchMailByKeyword(Integer page, Integer size, SortDirection sortDirection, String attribute, String keyword);

    Page<Mail> fetchMails(Integer page, Integer size, SortDirection sortDirection, String attribute);

    List<Mail> findMailByStateAndMailServer(MailState mailState, MailServer mailServer);

    void deleteMailByIds(List<Mail> mails);

    void completeMails(List<Mail> mails);

    Mail updateMail(String id, Mail mail);
}
