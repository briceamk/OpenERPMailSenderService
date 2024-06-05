package cm.xenonit.gelodia.openerpmailsender.notification.domain;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailState;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailTemplateType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import static java.time.LocalDateTime.now;

/**
 * @author bamk
 * @version 1.0
 * @since 11/02/2024
 */
@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_mail")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "c_disc_mail_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("mail")
public class Mail {

    @Id
    @UuidGenerator
    @Column(name = "c_id", unique = true, nullable = false)
    protected String id;
    @Column(name = "c_to", nullable = false)
    protected String to;
    @Column(name = "c_subject", length = 512, nullable = false)
    protected String subject;
    @Column(name = "c_message", columnDefinition = "TEXT")
    protected String message;
    @Column(name = "c_external_id", unique = true)
    protected Long externalId;
    @Column(name = "c_external_server_id")
    protected Long externalServerId;
    @Enumerated(EnumType.STRING)
    @Column(name = "c_state", nullable = false)
    protected MailState state;
    @Enumerated(EnumType.STRING)
    @Column(name = "c_type", nullable = false)
    protected MailTemplateType type;
    @Column(name = "c_created_at", nullable = false)
    protected LocalDateTime createdAt;
    @Column(name = "c_send_at")
    protected LocalDateTime sendAt;
    @Column(name = "c_attempt_to_send")
    private Integer attemptToSend;
    @ManyToOne
    @JoinColumn(name = "c_mail_template_id", nullable = false)
    protected MailTemplate mailTemplate;
    @ManyToOne
    @JoinColumn(name = "c_mail_server_id", nullable = false)
    protected MailServer mailServer;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "t_mail_attribute",
            joinColumns = {@JoinColumn(name = "c_mail_id", referencedColumnName = "c_id")}
    )
    @MapKeyColumn(name = "c_attribute_key")
    @Column(name = "c_attribute_value")
    protected Map<String, String> attributes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mail mail = (Mail) o;
        return Objects.equals(id, mail.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void initialize(MailTemplate mailTemplate, MailServer mailServer) {
        this.setMailServer(mailServer);
        this.setMailTemplate(mailTemplate);
        this.attemptToSend = this.attemptToSend == null? 0 : this.attemptToSend;
        if(this.createdAt == null)
            this.setCreatedAt(now());
        this.setState(MailState.SENDING);
    }

    public void sendSuccess() {
        this.setState(MailState.SEND);
        this.setSendAt(now());
    }

    public void sendFail() {
        this.setState(MailState.ERROR);
        this.setSendAt(now());
    }

    public void initialize(MailTemplate mailTemplate) {
        this.attemptToSend = this.attemptToSend == null? 0 : this.attemptToSend;
        this.setMailTemplate(mailTemplate);
    }

    public Mail sendComplete() {
        this.setState(MailState.COMPLETE);
        return this;
    }

    public void incrementAttemptToSend() {
        this.attemptToSend += 1;
    }
}
