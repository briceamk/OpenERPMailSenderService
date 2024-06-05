package cm.xenonit.gelodia.openerpmailsender.notification.domain;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailTemplateType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

/**
 * @author bamk
 * @version 1.0
 * @since 11/02/2024
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_mail_template")
public class MailTemplate {
    @Id
    @UuidGenerator
    @Column(name = "c_id", unique = true, nullable = false)
    private String id;
    @Column(name = "c_name", nullable = false, unique = true)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "c_type", nullable = false, unique = true)
    private MailTemplateType type;

}
