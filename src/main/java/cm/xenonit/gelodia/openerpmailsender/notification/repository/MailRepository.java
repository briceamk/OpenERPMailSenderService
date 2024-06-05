package cm.xenonit.gelodia.openerpmailsender.notification.repository;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.Mail;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailServer;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author bamk
 * @version 1.0
 * @since 24/01/2024
 */
public interface MailRepository extends JpaRepository<Mail, String> {
    List<Mail> findByStateIsIn(List<MailState> states);
    List<Mail> findByExternalIdNotNullAndState(MailState state);

    boolean existsByExternalId(Long externalId);
    @Query("SELECT e FROM Mail e WHERE LOWER((CONCAT(e.to, ' ', e.subject, ' ', e.message, ' ', CONCAT(e.externalId, ''), ' ', e.type, ' ', e.state, ' '))) LIKE %:keyword%")
    Page<Mail> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    List<Mail> findByStateAndMailServer(MailState mailState, MailServer mailServer);
}
