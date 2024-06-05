package cm.xenonit.gelodia.openerpmailsender.security.repository;

import cm.xenonit.gelodia.openerpmailsender.security.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
public interface UserRepository extends JpaRepository<User, String> {

    Integer countByEmailIgnoreCase(String email);

    Optional<User> findByEmail(String email);
}
