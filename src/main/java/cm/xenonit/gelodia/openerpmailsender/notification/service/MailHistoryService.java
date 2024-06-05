package cm.xenonit.gelodia.openerpmailsender.notification.service;

import cm.xenonit.gelodia.openerpmailsender.common.enums.SortDirection;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.Mail;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailHistory;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author bamk
 * @version 1.0
 * @since 19/02/2024
 */
public interface MailHistoryService {

    void createMailHistoriesFromMails(List<MailHistory> mailHistories);

    MailHistory fetchMailHistoryById(String mailHistoryId);

    Page<MailHistory> fetchMailHistoryByKeyword(Integer page, Integer size, SortDirection sortDirection, String attribute, String keyword);

    Page<MailHistory> fetchMailHistories(Integer page, Integer size, SortDirection sortDirection, String attribute);

}
