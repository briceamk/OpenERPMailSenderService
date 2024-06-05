package cm.xenonit.gelodia.openerpmailsender.security.domain;

import cm.xenonit.gelodia.openerpmailsender.security.domain.enums.VerificationStatus;
import cm.xenonit.gelodia.openerpmailsender.security.domain.enums.VerificationType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

import static cm.xenonit.gelodia.openerpmailsender.security.constant.SecurityConstant.*;
import static cm.xenonit.gelodia.openerpmailsender.security.domain.enums.VerificationStatus.*;
import static cm.xenonit.gelodia.openerpmailsender.security.domain.enums.VerificationType.Account;
import static cm.xenonit.gelodia.openerpmailsender.security.domain.enums.VerificationType.Mfa;
import static jakarta.persistence.EnumType.STRING;
import static java.time.LocalDateTime.now;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_verification")
public class Verification {

    @Id
    @UuidGenerator
    @Column(name = "c_id", unique = true, nullable = false)
    private String id;
    @Enumerated(STRING)
    @Column(name = "c_type", nullable = false)
    private VerificationType type;
    @Column(name = "c_code")
    private String code;
    @Column(name = "c_url", columnDefinition = "TEXT")
    private String url;
    @Column(name = "c_expiration")
    private LocalDateTime expiration;
    @Column(name = "c_created_at", nullable = false)
    private LocalDateTime createdAt;
    @Enumerated(STRING)
    @Column(name = "c_status", nullable = false)
    private VerificationStatus status;
    @ManyToOne
    @JoinColumn(name = "c_user_id", nullable = false)
    private User user;

    public void initialize(User user, String verificationCode, VerificationType type) {
        this.setType(type);
        this.setCreatedAt(now());
        this.setStatus(NotVerified);
        this.setUser(user);
        if(type.equals(Account)) {
            this.setUrl(verificationCode);
            this.setExpiration(now().plusMinutes(DEFAULT_ACCOUNT_EXPIRATION_IN_MINUTES));
        } else if(type.equals(Mfa)) {
            this.setCode(verificationCode);
            this.setExpiration(now().plusMinutes(DEFAULT_MFA_EXPIRATION_IN_MINUTES));
        } else {
            this.setUrl(verificationCode);
            this.setExpiration(now().plusMinutes(DEFAULT_PASSWORD_EXPIRATION_IN_MINUTES));
        }
    }

    public void validate() {
        this.setStatus(Verified);
    }

    public void cancelled() {
        this.setStatus(Cancelled);
    }
}
