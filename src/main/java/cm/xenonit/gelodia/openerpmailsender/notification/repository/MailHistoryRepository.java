package cm.xenonit.gelodia.openerpmailsender.notification.repository;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author bamk
 * @version 1.0
 * @since 19/02/2024
 */
public interface MailHistoryRepository extends JpaRepository<MailHistory, String> {
    @Query("SELECT mh FROM MailHistory mh WHERE LOWER((CONCAT(mh.to, ' ', mh.message, ' ',  mh.subject, ' ', mh.mailServer.name))) LIKE %:keyword%")
    Page<MailHistory> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
