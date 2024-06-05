package cm.xenonit.gelodia.openerpmailsender.security.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_role")
public class Role {
    @Id
    @UuidGenerator
    @Column(name = "c_id", unique = true, nullable = false)
    private String id;
    @Column(name = "c_name", nullable = false, unique = true)
    private String name;
    @Column(name = "c_permission", nullable = false, columnDefinition = "TEXT")
    private String permission;
}
