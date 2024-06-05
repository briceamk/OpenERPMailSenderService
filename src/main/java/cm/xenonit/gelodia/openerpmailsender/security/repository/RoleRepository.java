package cm.xenonit.gelodia.openerpmailsender.security.repository;

import cm.xenonit.gelodia.openerpmailsender.security.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(String name);
}
