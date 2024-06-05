package cm.xenonit.gelodia.openerpmailsender.openerp.domain;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailServer;
import cm.xenonit.gelodia.openerpmailsender.openerp.domain.enums.InstanceState;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

/**
 * @author bamk
 * @version 1.0
 * @since 21/02/2024
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "t_instance",
    uniqueConstraints = {
            @UniqueConstraint( name = "uk_t_instance_host_port_db", columnNames = {"host", "port", "db"})
    }
)
public class Instance {
    @Id
    @UuidGenerator
    @Column(name = "c_id", unique = true, nullable = false)
    private String id;
    @Column(name = "c_host", nullable = false)
    private String host;
    @Column(name = "c_port", nullable = false)
    private Integer port;
    @Column(name = "c_db", nullable = false)
    private String db;
    @Column(name = "c_username", nullable = false)
    private String username;
    @Column(name = "c_password", nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "c_state", nullable = false)
    private InstanceState state;
    @ManyToOne
    @JoinColumn(name = "c_mail_server_id", nullable = false)
    private MailServer mailServer;

    public void initialize() {
        if(this.state == null) {
            this.state = InstanceState.INACTIVE;
        }
    }

    public void activate() {
        this.state = InstanceState.ACTIVE;
    }

    public void inactive() {
        this.state = InstanceState.INACTIVE;
    }
}
