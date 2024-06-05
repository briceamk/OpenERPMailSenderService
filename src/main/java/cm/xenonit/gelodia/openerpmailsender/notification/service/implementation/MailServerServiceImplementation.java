package cm.xenonit.gelodia.openerpmailsender.notification.service.implementation;

import cm.xenonit.gelodia.openerpmailsender.common.enums.SortDirection;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailServer;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailServerState;
import cm.xenonit.gelodia.openerpmailsender.notification.event.VerifyMailServerNotificationEvent;
import cm.xenonit.gelodia.openerpmailsender.notification.exception.MailServerBadRequestException;
import cm.xenonit.gelodia.openerpmailsender.notification.exception.MailServerConflictException;
import cm.xenonit.gelodia.openerpmailsender.notification.exception.MailServerNotFoundException;
import cm.xenonit.gelodia.openerpmailsender.notification.repository.MailServerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;

import static cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailTemplateType.VERIFY_MAIL_SERVER;

/**
 * @author bamk
 * @version 1.0
 * @since 24/01/2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailServerServiceImplementation {

    public static final String EMAIL_VERIFY_MAIL_SERVER = "EmailSenderAPP: Validez votre serveur d'envoie de mail.";
    private final MailServerRepository mailServerRepository;
    private final ApplicationEventPublisher publisher;

    public MailServer createMailServer(MailServer mailServer) {
        mailServer.initialize();
        mailServer.validate();
        validateMailServer(null, mailServer);
        return mailServerRepository.save(mailServer);
    }

    public MailServer updateMailServer(String id, MailServer mailServer) {
        mailServer.validate();
        validateMailServer(id, mailServer);
        MailServer mailServerFromDB = fetchMailServerById(id);
        BeanUtils.copyProperties(mailServer, mailServerFromDB);
        return mailServerRepository.save(mailServerFromDB);
    }

    public MailServer fetchMailServerById(String emailServerId) {
        return mailServerRepository.findById(emailServerId)
                .orElseThrow(() -> new MailServerNotFoundException(String.format("Mail server with id '%s' not found.", emailServerId)));
    }
    public MailServer sendVerificationCode(String emailServerId, String email, String owner) {
        MailServer mailServer = fetchMailServerById(emailServerId);
        if(!mailServer.getState().equals(MailServerState.NEW))
            throw new MailServerBadRequestException(
                    String.format("You can't get verification code for a server in state is %s.", mailServer.getState().name()));
        mailServer.generateVerificationCode();
        mailServer = mailServerRepository.save(mailServer);
        publishVerifyMailServerEventNotification(mailServer, email, owner);
        return mailServer;
    }

    private void validateMailServer(String id, MailServer mailServer) {
        //TODO validate unique in create and update
        if(mailServerRepository.countByNameEqualsIgnoreCase(mailServer.getName()) > 0) {
            throw new MailServerConflictException(String.format(
                    "An email server with name %s already exist. Please try with another name.", mailServer.getName()));
        }
    }


    public MailServer verifyMailServer(String emailServerId, String code) {
        MailServer mailServer = fetchMailServerById(emailServerId);
        if(!mailServer.getState().equals(MailServerState.WAITING))
            throw new MailServerBadRequestException(
                    String.format("You can't not verify a server with state %s.", mailServer.getState().name()));
        if(!code.equals(mailServer.getVerificationCode()))
            throw new MailServerBadRequestException(
                    String.format("Your verification code '%s' is incorrect. Please try again", code));
        mailServer.verify();
        return mailServerRepository.save(mailServer);

    }

    public Page<MailServer> fetchMailServers(int page, int size, SortDirection sortDirection, String attribute) {
        Sort.Direction direction = sortDirection.equals(SortDirection.ASC) ? Sort.Direction.ASC: Sort.Direction.DESC;
        //TODO Show all by filter by privileges
        //return mailServerRepository.findAll(PageRequest.of(page, size, direction, attribute));
        return mailServerRepository.findByUseAsDefaultIsFalse(PageRequest.of(page, size, direction, attribute));
    }

    public Page<MailServer> fetchMailServerByKeyword(int page, int size, SortDirection sortDirection, String attribute, String keyword) {
        Sort.Direction direction = sortDirection.equals(SortDirection.ASC) ? Sort.Direction.ASC: Sort.Direction.DESC;
        return mailServerRepository.findByKeyword(keyword.toLowerCase(), PageRequest.of(page, size, direction, attribute));
    }

    public JavaMailSender getMailSenderInstance(MailServer mailServer) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailServer.getHost());
        mailSender.setPort(mailServer.getPort());
        if(mailServer.getUseAuth()) {
            mailSender.setUsername(mailServer.getUsername());
            mailSender.setPassword(mailServer.getPassword());
        }
        mailSender.setDefaultEncoding(StandardCharsets.UTF_8.name());

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol", mailServer.getProtocol());
        properties.put("mail.smtp.auth", String.valueOf(mailServer.getUseAuth()));
        properties.put("mail.smtp.starttls.enable", String.valueOf(mailServer.getUseSSL()));

        return mailSender;
    }

    public MailServer findDefaultMailServer() {
        return mailServerRepository.findByUseAsDefaultTrue().orElseThrow(
                () -> new MailServerNotFoundException("Default mail server not found.")
        );
    }

    public Optional<MailServer> findFirstNonDefaultServer() {
        return mailServerRepository.findByUseAsDefaultIsNullOrUseAsDefaultIsFalse().stream().findFirst();
    }


    private void publishVerifyMailServerEventNotification(MailServer mailServer, String email, String owner) {
        publisher.publishEvent(new VerifyMailServerNotificationEvent(
                        email, EMAIL_VERIFY_MAIL_SERVER, VERIFY_MAIL_SERVER, owner, mailServer.getVerificationCode()));
    }
}
