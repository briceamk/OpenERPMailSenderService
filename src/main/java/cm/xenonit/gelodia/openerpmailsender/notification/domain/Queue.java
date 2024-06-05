package cm.xenonit.gelodia.openerpmailsender.notification.domain;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailState;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author bamk
 * @version 1.0
 * @since 24/01/2024
 */

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_queue")
public class Queue {
    @Id
    @UuidGenerator
    @Column(name = "c_id", unique = true, nullable = false)
    private String id;
    @Column(name = "c_message", nullable = false, columnDefinition = "TEXT")
    private String message;
    @Column(name = "c_external_id", nullable = false, unique = true)
    private Long externalId;
    @Enumerated(EnumType.STRING)
    @Column(name = "c_state", nullable = false)
    private MailState state;
    @Column(name = "c_created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "c_send_at", nullable = false)
    private LocalDateTime sendAt;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Queue queue = (Queue) o;
        return Objects.equals(id, queue.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
