package cm.xenonit.gelodia.openerpmailsender.notification.repository;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailServer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author bamk
 * @version 1.0
 * @since 24/01/2024
 */
public interface MailServerRepository extends JpaRepository<MailServer, String> {
    Integer countByNameEqualsIgnoreCase(String name);
    @Query("SELECT e FROM MailServer e WHERE LOWER((CONCAT(e.name, ' ', e.fromEmail, ' ', e.host, ' ', CONCAT(e.port, ''), ' ', e.username, ' ', e.type, ' ', e.state, ' '))) LIKE %:keyword%")
    Page<MailServer> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<MailServer> findByUseAsDefaultIsFalse(Pageable pageable);
    List<MailServer> findByUseAsDefaultIsNullOrUseAsDefaultIsFalse();

    Optional<MailServer> findByUseAsDefaultTrue();
}
