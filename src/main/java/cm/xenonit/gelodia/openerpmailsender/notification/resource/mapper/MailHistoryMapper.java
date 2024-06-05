package cm.xenonit.gelodia.openerpmailsender.notification.resource.mapper;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailHistory;
import cm.xenonit.gelodia.openerpmailsender.notification.resource.dto.MailHistoryDto;
import cm.xenonit.gelodia.openerpmailsender.notification.resource.dto.MailServerDto;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author bamk
 * @version 1.0
 * @since 19/02/2024
 */
@Component
public class MailHistoryMapper {

    public MailHistoryDto fromMailHistory(MailHistory mailHistory) {
        MailHistoryDto mailHistoryDto = new MailHistoryDto();
        MailServerDto mailServerDto = new MailServerDto();
        BeanUtils.copyProperties(mailHistory, mailHistoryDto);
        BeanUtils.copyProperties(mailHistory.getMailServer(), mailServerDto);
        mailHistoryDto.setMailServer(mailServerDto);
        return mailHistoryDto;
    }

    public List<MailHistoryDto> fromMailHistoryPage(Page<MailHistory> mailHistoryPage) {
        List<MailHistory> mailHistories = mailHistoryPage.getContent();
        return mailHistories.stream().map(this::fromMailHistory).toList();
    }
}
