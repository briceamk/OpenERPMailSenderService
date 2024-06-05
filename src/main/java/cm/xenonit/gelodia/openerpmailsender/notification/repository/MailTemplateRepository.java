package cm.xenonit.gelodia.openerpmailsender.notification.repository;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailTemplate;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailTemplateType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author bamk
 * @version 1.0
 * @since 24/01/2024
 */
public interface MailTemplateRepository extends JpaRepository<MailTemplate, String> {
    Optional<MailTemplate> findByType(MailTemplateType type);

}
