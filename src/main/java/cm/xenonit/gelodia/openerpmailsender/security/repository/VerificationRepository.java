package cm.xenonit.gelodia.openerpmailsender.security.repository;

import cm.xenonit.gelodia.openerpmailsender.security.domain.User;
import cm.xenonit.gelodia.openerpmailsender.security.domain.Verification;
import cm.xenonit.gelodia.openerpmailsender.security.domain.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
public interface VerificationRepository extends JpaRepository<Verification, String> {
    Optional<Verification> findByUrl(String verificationUrl);

    Optional<Verification> findByUserAndCode(User user, String code);

    Optional<Verification> findByUserAndStatus(User user, VerificationStatus status);
}
