package cm.xenonit.gelodia.openerpmailsender.notification.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

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
@Table(name = "t_mail_history")
public class MailHistory {

    @Id
    @UuidGenerator
    @Column(name = "c_id", unique = true, nullable = false)
    private String id;
    @Column(name = "c_to", nullable = false)
    private String to;
    @Column(name = "c_subject", length = 512, nullable = false)
    private String subject;
    @Column(name = "c_message", columnDefinition = "TEXT")
    private String message;
    @Column(name = "c_created_at", nullable = false)
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "c_mail_server_id", nullable = false)
    private MailServer mailServer;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "t_mail_history_attribute",
            joinColumns = {@JoinColumn(name = "c_mail_history_id", referencedColumnName = "c_id")}
    )
    @MapKeyColumn(name = "c_attribute_key")
    @Column(name = "c_attribute_value")
    private Map<String, String> attributes;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MailHistory mail = (MailHistory) o;
        return Objects.equals(id, mail.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
