package cm.xenonit.gelodia.openerpmailsender.notification.service.implementation;

import cm.xenonit.gelodia.openerpmailsender.common.enums.SortDirection;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.Mail;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailServer;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailTemplate;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailServerState;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailState;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailTemplateType;
import cm.xenonit.gelodia.openerpmailsender.notification.exception.MailInternalServerErrorException;
import cm.xenonit.gelodia.openerpmailsender.notification.exception.MailNotFoundException;
import cm.xenonit.gelodia.openerpmailsender.notification.exception.MailSenderBadException;
import cm.xenonit.gelodia.openerpmailsender.notification.exception.MailTemplateNotFoundException;
import cm.xenonit.gelodia.openerpmailsender.notification.repository.MailRepository;
import cm.xenonit.gelodia.openerpmailsender.notification.repository.MailTemplateRepository;
import cm.xenonit.gelodia.openerpmailsender.notification.resource.dto.MailDto;
import cm.xenonit.gelodia.openerpmailsender.notification.service.MailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author bamk
 * @version 1.0
 * @since 11/02/2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImplementation implements MailService {

    private final SpringTemplateEngine templateEngine;
    private final MailServerServiceImplementation mailServerService;
    private final MailRepository mailRepository;
    private final MailTemplateRepository mailTemplateRepository;

    @Override
    public Mail createEmail(Mail mail) {
        MailTemplate mailTemplate = fetchMailTemplate(mail.getType());
        if(mail.getMailServer() == null) {
            MailServer mailServer = mailServerService.findDefaultMailServer();
            mail.initialize(mailTemplate, mailServer);
        } else {
            mail.initialize(mailTemplate);
        }
        return mailRepository.save(mail);
    }

    @Override
    @Transactional(readOnly = true)
    public Mail findById(String id) {
        return mailRepository.findById(id).orElseThrow(
                () -> new MailNotFoundException(String.format("Mail with id '%s' not found.", id))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mail> findMailWithExternalIdToComplete() {
        return mailRepository.findByExternalIdNotNullAndState(MailState.SEND);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean mailExistByExternalId(Long remoteMailId) {
        return mailRepository.existsByExternalId(remoteMailId);
    }

    @Override
    @Transactional(readOnly = true)
    public Mail fetchMailById(String id) {
        return mailRepository.findById(id).orElseThrow(
                () -> new MailNotFoundException(String.format("Mail with id '%s' not found!", id))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Mail> fetchMailByKeyword(Integer page, Integer size, SortDirection sortDirection, String attribute, String keyword) {
        Sort.Direction direction = sortDirection.equals(SortDirection.ASC) ? Sort.Direction.ASC: Sort.Direction.DESC;
        return mailRepository.findByKeyword(keyword.toLowerCase(), PageRequest.of(page, size, direction, attribute));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Mail> fetchMails(Integer page, Integer size, SortDirection sortDirection, String attribute) {
        Sort.Direction direction = sortDirection.equals(SortDirection.ASC) ? Sort.Direction.ASC: Sort.Direction.DESC;
        return mailRepository.findAll(PageRequest.of(page, size, direction, attribute));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Mail> findMailByStateAndMailServer(MailState mailState, MailServer mailServer) {
        return mailRepository.findByStateAndMailServer(mailState, mailServer);
    }

    @Override
    @Transactional
    public void deleteMailByIds(List<Mail> mails) {
        mailRepository.deleteAll(mails);
    }

    @Override
    public void completeMails(List<Mail> mails) {
        mailRepository.saveAll(mails.stream().map(Mail::sendComplete).toList());
    }

    @Override
    @Transactional
    public Mail updateMail(String id, Mail mail) {
        Mail dbMail = findById(id);
        BeanUtils.copyProperties(mail, dbMail);
        return mailRepository.save(dbMail);
    }

    @Transactional(noRollbackFor = {RuntimeException.class, Exception.class})
    public void sendMail(Mail mail)  {
        MailServer mailServer = mail.getMailServer();
        if(!mailServer.getState().equals(MailServerState.CONFIRM)) {
            mail.sendFail();
            throw new MailSenderBadException("Can't send an mail with unverified server. Please verify your server and try again");
        }
        if(mail.getAttemptToSend() >= 2) {
            mail.sendFail();
            mailRepository.save(mail);
        } else {
            JavaMailSender mailSenderInstance = mailServerService.getMailSenderInstance(mailServer);
            MimeMessage message = mailSenderInstance.createMimeMessage();
            String htmlEmail = mail.getMailTemplate().getType().equals(MailTemplateType.NOT_APPLICABLE)
                    ? mail.getMessage()
                    : Objects.requireNonNull(getEmailInHtmlContent(mail));

            try {
                MimeMessageHelper messageHelper = new MimeMessageHelper(
                        message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
                mail.incrementAttemptToSend();
                mailRepository.save(mail);
                messageHelper.setTo(mail.getTo());
                messageHelper.setFrom(mailServer.getFromEmail());
                messageHelper.setSubject(mail.getSubject());
                messageHelper.setText(htmlEmail, true);
                mailSenderInstance.send(message);
                mail.sendSuccess();
            } catch (Exception e) {
                mail.sendFail();
                throw new MailInternalServerErrorException(e.getMessage());
            } finally {
                mailRepository.save(mail);
            }
        }


    }
    @Override
    public void sendMails() {
        log.info(">>>> Start sending email in queue...");
        mailRepository
                .findByStateIsIn(List.of(MailState.SENDING, MailState.ERROR, MailState.DRAFT))
                .forEach(this::sendMail);
        log.info(">>>> End sending email in queue...");
    }

    private MailTemplate fetchMailTemplate(MailTemplateType type) {
        return mailTemplateRepository.findByType(type).orElseThrow(
                () -> new MailTemplateNotFoundException(String.format("Mail template for type %s not found.", type.getType()))
        );
    }

    private String getEmailInHtmlContent(Mail mail) {
        Context context = new Context();
        Map<String, Object> variables = mail.getAttributes().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        context.setVariables(variables);
        return templateEngine.process(mail.getMailTemplate().getName(), context);
    }
}
