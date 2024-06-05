package cm.xenonit.gelodia.openerpmailsender.notification.domain;

import cm.xenonit.gelodia.openerpmailsender.notification.constant.GoogleConstant;
import cm.xenonit.gelodia.openerpmailsender.notification.constant.YahooConstant;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailServerState;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailServerType;
import cm.xenonit.gelodia.openerpmailsender.notification.exception.MailServerBadRequestException;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static cm.xenonit.gelodia.openerpmailsender.notification.constant.NotificationConstant.DEFAULT_SMTP_PROTOCOL;
import static java.time.LocalDate.now;

/**
 * @author bamk
 * @version 1.0
 * @since 24/01/2024
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_mail_server")
public class MailServer {
    @Id
    @UuidGenerator
    @Column(name = "c_id", unique = true, nullable = false)
    private String id;
    @Column(name = "c_name", nullable = false, unique = true)
    private String name;
    @Column(name = "c_from_email", nullable = false)
    private String fromEmail;
    @Column(name = "c_created_at", nullable = false)
    private LocalDate createdAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "c_type", nullable = false)
    private MailServerType type;
    @Column(name = "c_host", nullable = false)
    private String host;
    @Column(name = "c_port", nullable = false)
    private Integer port;
    @Column(name = "c_protocol", nullable = false)
    private String protocol;
    @Column(name = "c_use_ssl")
    private Boolean useSSL;
    @Column(name = "c_use_auth")
    private Boolean useAuth;
    @Column(name = "c_use_as_default")
    private Boolean useAsDefault;
    @Column(name = "c_username")
    private String username;
    @Column(name = "c_password")
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "c_state", nullable = false)
    private MailServerState state;
    @Column(name = "c_verification_code", length = 128)
    private String verificationCode;
    @Column(name = "c_verified_at")
    private LocalDateTime verifiedAt;


    public void validate() {
        if(this.getFromEmail() == null || this.fromEmail.isEmpty()) {
            throw new MailServerBadRequestException("From email is required. Please provide an email an try again.");
        }
        if(this.getUseAuth() != null && this.getUseAuth() && (this.getUsername() == null || this.getUsername().isEmpty())) {
            throw new MailServerBadRequestException("Username is required when using auth is active. Please provide a username");
        }
        if(this.getUseAuth() != null && this.getUseAuth() && (this.getPassword() == null || this.getPassword().isEmpty())) {
            throw new MailServerBadRequestException("Password is required when using auth is active. Please provide a password");
        }
    }

    public void initialize() {
        this.setCreatedAt(now());
        this.setState(MailServerState.NEW);
        this.setProtocol(DEFAULT_SMTP_PROTOCOL);
        if(this.type.equals(MailServerType.GOOGLE)) {
            this.setHost(GoogleConstant.HOST);
            this.setPort(GoogleConstant.PORT);
            this.setUseSSL(GoogleConstant.USE_SSL);
            this.setUseAuth(GoogleConstant.USE_AUTH);
            this.setProtocol(GoogleConstant.PROTOCOL);
        }
        if(this.type.equals(MailServerType.YAHOO)) {
            this.setHost(YahooConstant.HOST);
            this.setPort(YahooConstant.PORT);
            this.setUseSSL(YahooConstant.USE_SSL);
            this.setUseAuth(YahooConstant.USE_AUTH);
            this.setProtocol(YahooConstant.PROTOCOL);
        }
        if(this.getUseAuth() == null)
            this.setUseAuth(false);
        if(this.getUseSSL() == null)
            this.setUseSSL(false);
        if(this.getUseAsDefault() == null)
            this.setUseAsDefault(false);
    }

    public void generateVerificationCode() {
        this.setVerificationCode(RandomStringUtils.randomAlphanumeric(64));
        this.setState(MailServerState.WAITING);
    }

    public void verify() {
        this.setState(MailServerState.CONFIRM);
        this.setVerifiedAt(LocalDateTime.now());
    }
}
