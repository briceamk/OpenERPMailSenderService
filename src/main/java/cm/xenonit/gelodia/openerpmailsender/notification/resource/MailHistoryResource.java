package cm.xenonit.gelodia.openerpmailsender.notification.resource;

import cm.xenonit.gelodia.openerpmailsender.common.dto.PageInfoDto;
import cm.xenonit.gelodia.openerpmailsender.common.enums.SortDirection;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailHistory;
import cm.xenonit.gelodia.openerpmailsender.notification.generated.resource.MailHistoriesApi;
import cm.xenonit.gelodia.openerpmailsender.notification.generated.resource.dto.ApiSuccessResponseDto;
import cm.xenonit.gelodia.openerpmailsender.notification.resource.mapper.MailHistoryMapper;
import cm.xenonit.gelodia.openerpmailsender.notification.service.MailHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author bamk
 * @version 1.0
 * @since 24/01/2024
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class MailHistoryResource implements MailHistoriesApi {

    private final MailHistoryService mailHistoryService;
    private final MailHistoryMapper mailHistoryMapper;

    

    @Override
    @PreAuthorize("hasAuthority('read:mail_history')")
    public ResponseEntity<ApiSuccessResponseDto> fetchMailHistoryById(String mailHistoryId) {
        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Mail History  found.")
                                .success(true)
                                .data(of("data", mailHistoryMapper.fromMailHistory(mailHistoryService.fetchMailHistoryById(mailHistoryId))))
                );
    }

    @Override
    @PreAuthorize("hasAuthority('read:mail_histroy')")
    public ResponseEntity<ApiSuccessResponseDto> fetchMailHistoryByKeyword(Integer page, Integer size, String direction, String attribute, String keyword) {
        Page<MailHistory> mailHistoryPage = mailHistoryService.fetchMailHistoryByKeyword(page, size, SortDirection.valueOf(direction), attribute, keyword);
        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Mail Histories found.")
                                .success(true)
                                .data(
                                        of(
                                                "data", mailHistoryMapper.fromMailHistoryPage(mailHistoryPage),
                                                "pageInfo", PageInfoDto.builder()
                                                        .first(mailHistoryPage.isFirst())
                                                        .last(mailHistoryPage.isLast())
                                                        .totalElements(mailHistoryPage.getTotalElements())
                                                        .totalPages(mailHistoryPage.getTotalPages())
                                                        .pageSize(size)
                                                        .build()
                                        )
                                )
                );
    }

    @Override
    @PreAuthorize("hasAuthority('read:mail_history')")
    public ResponseEntity<ApiSuccessResponseDto> fetchMailHistories(Integer page, Integer size, String direction, String attribute) {
        Page<MailHistory> mailHistoryPage = mailHistoryService.fetchMailHistories(page, size, SortDirection.valueOf(direction), attribute);
        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Mails found.")
                                .success(true)
                                .data(
                                        of(
                                            "data", mailHistoryMapper.fromMailHistoryPage(mailHistoryPage),
                                            "pageInfo", PageInfoDto.builder()
                                                        .first(mailHistoryPage.isFirst())
                                                        .last(mailHistoryPage.isLast())
                                                        .totalElements(mailHistoryPage.getTotalElements())
                                                        .totalPages(mailHistoryPage.getTotalPages())
                                                        .pageSize(size)
                                                        .build()
                                        )
                                )
                );
    }
    
}
