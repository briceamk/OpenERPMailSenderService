package cm.xenonit.gelodia.openerpmailsender.security.domain;

import cm.xenonit.gelodia.openerpmailsender.security.exception.UserBadRequestException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Set;

import static cm.xenonit.gelodia.openerpmailsender.security.constant.SecurityConstant.DEFAULT_IMAGE_URL;
import static jakarta.persistence.FetchType.EAGER;
import static java.time.LocalDateTime.now;
import static java.util.Set.of;

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
@Table(name = "t_user")
public class User {
    @Id
    @UuidGenerator
    @Column(name = "c_id", unique = true, nullable = false)
    private String id;
    @Column(name = "c_first_name")
    private String firstName;
    @Column(name = "c_last_name", nullable = false)
    private String lastName;
    @Column(name = "c_email", nullable = false, unique = true)
    private String email;
    @Column(name = "c_password", nullable = false, length = 512)
    private String password;
    @Column(name = "c_phone", unique = true)
    private String phone;
    @Column(name = "c_image_url", nullable = false)
    private String imageUrl;
    @Column(name = "c_account_enabled", nullable = false)
    private Boolean accountEnabled;
    @Column(name = "c_account_not_locked", nullable = false)
    private Boolean accountNotLocked;
    @Column(name = "c_account_not_expired", nullable = false)
    private Boolean accountNotExpired;
    @Column(name = "c_credential_not_expired", nullable = false)
    private Boolean credentialsNotExpired;
    @Column(name = "c_use_mfa", nullable = false)
    private Boolean useMfa;
    @Column(name = "c_created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "c_failed_login_attempt")
    private Integer failedLoginAttempt;
    @Transient
    private String confirmPassword;
    @ManyToMany(fetch = EAGER)
    @JoinTable(
            name = "t_user_role",
            joinColumns = @JoinColumn(name = "c_user_id", referencedColumnName = "c_id"),
            inverseJoinColumns = @JoinColumn(name = "c_role_id", referencedColumnName = "c_id")
    )
    Set<Role> roles;

    public void initialize() {
        this.setAccountEnabled(false);
        this.setAccountNotLocked(true);
        this.setAccountNotExpired(true);
        this.setCredentialsNotExpired(true);
        this.setCreatedAt(now());
        if(this.getUseMfa() == null)
            this.setUseMfa(false);
        this.setImageUrl(DEFAULT_IMAGE_URL);
        this.setFailedLoginAttempt(0);
    }

    public void cryptPassword(String encryptedPassword) {
        this.setPassword(encryptedPassword);
    }

    public void addRole(Role role) {
        this.setRoles(of(role));
    }

    public void enableAccount() {
        this.setAccountEnabled(true);
    }

    public void disableAccount() {
        this.setAccountEnabled(false);
    }

    public void registerFailedLoginAttempt(int counter) {
        this.setFailedLoginAttempt(counter);
    }

    public void resetFailedLoginAttempt() {
        this.setFailedLoginAttempt(0);
    }

    public void verifyPassword() {
        if(!this.password.equals(this.confirmPassword)) {
            throw new UserBadRequestException("Password and confirm password are not the same. Please try again.");
        }
    }

    public void verifyPassword(String password, String confirmPassword) {
        if(!password.equals(confirmPassword)) {
            throw new UserBadRequestException("Password and confirm password are not the same. Please try again.");
        }
    }
}
