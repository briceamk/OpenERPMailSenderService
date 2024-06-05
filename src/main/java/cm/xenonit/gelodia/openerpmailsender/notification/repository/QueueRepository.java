package cm.xenonit.gelodia.openerpmailsender.notification.repository;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.Queue;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author bamk
 * @version 1.0
 * @since 24/01/2024
 */
public interface QueueRepository extends JpaRepository<Queue, String> {
}
