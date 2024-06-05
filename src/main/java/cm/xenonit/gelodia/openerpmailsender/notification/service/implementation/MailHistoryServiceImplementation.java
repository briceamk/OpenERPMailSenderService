package cm.xenonit.gelodia.openerpmailsender.notification.service.implementation;

import cm.xenonit.gelodia.openerpmailsender.common.enums.SortDirection;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.Mail;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailHistory;
import cm.xenonit.gelodia.openerpmailsender.notification.exception.MailHistoryNotFoundException;
import cm.xenonit.gelodia.openerpmailsender.notification.repository.MailHistoryRepository;
import cm.xenonit.gelodia.openerpmailsender.notification.service.MailHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author bamk
 * @version 1.0
 * @since 19/02/2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailHistoryServiceImplementation implements MailHistoryService {

    private final MailHistoryRepository mailHistoryRepository;

    @Override
    public void createMailHistoriesFromMails(List<MailHistory> mailHistories) {
        mailHistoryRepository.saveAll(mailHistories);

    }

    @Override
    public MailHistory fetchMailHistoryById(String mailHistoryId) {
        return mailHistoryRepository.findById(mailHistoryId).orElseThrow(
                () -> new MailHistoryNotFoundException("Mail history with id '%' not found")
        );
    }

    @Override
    public Page<MailHistory> fetchMailHistoryByKeyword(Integer page, Integer size, SortDirection sortDirection, String attribute, String keyword) {
        Sort.Direction direction = sortDirection.equals(SortDirection.ASC) ? Sort.Direction.ASC: Sort.Direction.DESC;
        return mailHistoryRepository.findByKeyword(keyword.toLowerCase(), PageRequest.of(page, size, direction, attribute));
    }

    @Override
    public Page<MailHistory> fetchMailHistories(Integer page, Integer size, SortDirection sortDirection, String attribute) {
        Sort.Direction direction = sortDirection.equals(SortDirection.ASC) ? Sort.Direction.ASC: Sort.Direction.DESC;
        return mailHistoryRepository.findAll(PageRequest.of(page, size, direction, attribute));
    }
}
