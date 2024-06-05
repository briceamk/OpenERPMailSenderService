package cm.xenonit.gelodia.openerpmailsender.notification.resource;

import cm.xenonit.gelodia.openerpmailsender.common.dto.PageInfoDto;
import cm.xenonit.gelodia.openerpmailsender.common.enums.SortDirection;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.Mail;
import cm.xenonit.gelodia.openerpmailsender.notification.generated.resource.MailsApi;
import cm.xenonit.gelodia.openerpmailsender.notification.generated.resource.dto.ApiSuccessResponseDto;
import cm.xenonit.gelodia.openerpmailsender.notification.generated.resource.dto.UpdateMailRequestDto;
import cm.xenonit.gelodia.openerpmailsender.notification.resource.mapper.MailMapper;
import cm.xenonit.gelodia.openerpmailsender.notification.service.implementation.MailServiceImplementation;
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
public class MailResource implements MailsApi {

    private final MailServiceImplementation mailService;
    private final MailMapper mailMapper;

    

    @Override
    @PreAuthorize("hasAuthority('read:mail')")
    public ResponseEntity<ApiSuccessResponseDto> fetchMailById(String mailId) {
        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Mail found.")
                                .success(true)
                                .data(of("data", mailMapper.fromMail(
                                        mailService.fetchMailById(mailId))))
                );
    }

    @Override
    @PreAuthorize("hasAuthority('read:mail')")
    public ResponseEntity<ApiSuccessResponseDto> fetchMailByKeyword(Integer page, Integer size, String direction, String attribute, String keyword) {
        Page<Mail> mailPage = mailService.fetchMailByKeyword(page, size, SortDirection.valueOf(direction), attribute, keyword);
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
                                                "data", mailMapper.fromMailPage(mailPage),
                                                "pageInfo", PageInfoDto.builder()
                                                        .first(mailPage.isFirst())
                                                        .last(mailPage.isLast())
                                                        .totalElements(mailPage.getTotalElements())
                                                        .totalPages(mailPage.getTotalPages())
                                                        .pageSize(size)
                                                        .build()
                                        )
                                )
                );
    }

    @Override
    @PreAuthorize("hasAuthority('read:mail')")
    public ResponseEntity<ApiSuccessResponseDto> fetchMails(Integer page, Integer size, String direction, String attribute) {
        Page<Mail> mailPage = mailService.fetchMails(page, size, SortDirection.valueOf(direction), attribute);
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
                                            "data", mailMapper.fromMailPage(mailPage),
                                            "pageInfo", PageInfoDto.builder()
                                                        .first(mailPage.isFirst())
                                                        .last(mailPage.isLast())
                                                        .totalElements(mailPage.getTotalElements())
                                                        .totalPages(mailPage.getTotalPages())
                                                        .pageSize(size)
                                                        .build()
                                        )
                                )
                );
    }

    @Override
    public ResponseEntity<ApiSuccessResponseDto> updateMailById(String mailId, UpdateMailRequestDto updateMailRequestDto) {

        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                 .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Mail updated successfully.")
                                .success(true)
                                .data(
                                        of("data", mailMapper.fromMail(mailService.updateMail(mailId, mailMapper.fromUpdateMailRequestDto(updateMailRequestDto))))
                                )
                );
    }

}
