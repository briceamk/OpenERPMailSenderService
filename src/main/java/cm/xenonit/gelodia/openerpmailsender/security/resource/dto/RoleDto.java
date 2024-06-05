package cm.xenonit.gelodia.openerpmailsender.security.resource.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    @NotBlank(message = "id is required.")
    private String id;
    @NotBlank(message = "role name is required")
    private String name;
    @NotBlank(message = "permissions is required.")
    private String permission;
}
