package cm.xenonit.gelodia.openerpmailsender.notification.resource;

import cm.xenonit.gelodia.openerpmailsender.common.CommonUtils;
import cm.xenonit.gelodia.openerpmailsender.common.dto.PageInfoDto;
import cm.xenonit.gelodia.openerpmailsender.common.enums.SortDirection;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailServer;
import cm.xenonit.gelodia.openerpmailsender.notification.generated.resource.MailServersApi;
import cm.xenonit.gelodia.openerpmailsender.notification.generated.resource.dto.*;
import cm.xenonit.gelodia.openerpmailsender.notification.resource.dto.MailServerDto;
import cm.xenonit.gelodia.openerpmailsender.notification.resource.mapper.MailServerMapper;
import cm.xenonit.gelodia.openerpmailsender.notification.service.implementation.MailServerServiceImplementation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author bamk
 * @version 1.0
 * @since 24/01/2024
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class MailServerResource implements MailServersApi {

    private final MailServerServiceImplementation mailServerService;
    private final MailServerMapper mailServerMapper;

    @Override
    @PreAuthorize("hasAuthority('create:mail_server')")
    public ResponseEntity<ApiSuccessResponseDto> createMailServer(CreateMailServerDto mailServerDto) {
        MailServer mailServer = mailServerMapper.fromCreateMailServerDto(mailServerDto);
        MailServerDto mailServerResponseDto = mailServerMapper.fromMailServer(
                mailServerService.createMailServer(mailServer));
        return ResponseEntity
                .status(CREATED)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(CREATED.value())
                                .status(CREATED.getReasonPhrase())
                                .message("Mail server created successfully")
                                .success(true)
                                .data(of("data", mailServerResponseDto))
                );
    }

    @Override
    @PreAuthorize("hasAuthority('read:mail_server')")
    public ResponseEntity<ApiSuccessResponseDto> fetchMailServerById(String mailServerId) {
        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Mail server found.")
                                .success(true)
                                .data(of("data", mailServerMapper.fromMailServer(
                                        mailServerService.fetchMailServerById(mailServerId))))
                );
    }

    @Override
    @PreAuthorize("hasAuthority('read:mail_server')")
    public ResponseEntity<ApiSuccessResponseDto> fetchMailServerByKeyword(Integer page, Integer size, String direction, String attribute, String keyword) {
        Page<MailServer> mailServerPage = mailServerService.fetchMailServerByKeyword(page, size, SortDirection.valueOf(direction), attribute, keyword);
        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Mails servers found.")
                                .success(true)
                                .data(
                                        of(
                                                "data", mailServerMapper.fromMailServers(mailServerPage),
                                                "pageInfo", PageInfoDto.builder()
                                                        .first(mailServerPage.isFirst())
                                                        .last(mailServerPage.isLast())
                                                        .totalElements(mailServerPage.getTotalElements())
                                                        .totalPages(mailServerPage.getTotalPages())
                                                        .pageSize(size)
                                                        .build()
                                        )
                                )
                );
    }

    @Override
    @PreAuthorize("hasAuthority('read:mail_server')")
    public ResponseEntity<ApiSuccessResponseDto> fetchMailServers(Integer page, Integer size, String direction, String attribute) {
        Page<MailServer> mailServerPage = mailServerService.fetchMailServers(page, size, SortDirection.valueOf(direction), attribute);
        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Mails servers found.")
                                .success(true)
                                .data(
                                        of(
                                            "data", mailServerMapper.fromMailServers(mailServerPage),
                                            "pageInfo", PageInfoDto.builder()
                                                        .first(mailServerPage.isFirst())
                                                        .last(mailServerPage.isLast())
                                                        .totalElements(mailServerPage.getTotalElements())
                                                        .totalPages(mailServerPage.getTotalPages())
                                                        .pageSize(size)
                                                        .build()
                                        )
                                )
                );
    }

    @Override
    @PreAuthorize("hasAuthority('update:send_code:mail_server')")
    public ResponseEntity<ApiSuccessResponseDto> sendCode(String mailServerId, RecipientDto recipientDto) {
        String email = CommonUtils.maskEmail(recipientDto.getEmail());
        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message(String.format("Verification code has send to %s the recipient you have specified", email))
                                .success(true)
                                .data(of("data", mailServerService.sendVerificationCode(mailServerId, recipientDto.getEmail(), recipientDto.getOwner())))
                );
    }

    @Override
    @PreAuthorize("hasAuthority('update:mail_server')")
    public ResponseEntity<ApiSuccessResponseDto> updateMailServer(String mailServerId, UpdateMailServerDto updateMailServerDto) {
        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Mail updated successfully.")
                                .success(true)
                                .data(of("data", mailServerMapper.fromMailServer(
                                                mailServerService.updateMailServer(mailServerId,
                                                mailServerMapper.fromUpdateMailServerDto(updateMailServerDto))))
                                )
                );
    }

    @Override
    @PreAuthorize("hasAuthority('update:verify_code:mail_server')")
    public ResponseEntity<ApiSuccessResponseDto> verifyCode(String mailServerId, CodeVerificationDto verificationDtoDto) {

        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Mail server is now verified. It can be use to send email now.")
                                .success(true)
                                .data(of("data", mailServerService.verifyMailServer(mailServerId, verificationDtoDto.getCode())))
                );
    }
}
