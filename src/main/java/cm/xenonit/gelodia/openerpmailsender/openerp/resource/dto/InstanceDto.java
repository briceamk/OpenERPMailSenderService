package cm.xenonit.gelodia.openerpmailsender.openerp.resource.dto;

import cm.xenonit.gelodia.openerpmailsender.instance.generated.resource.dto.MailServerDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author bamk
 * @version 1.0
 * @since 21/02/2024
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InstanceDto {
    @NotBlank(message = "id is required")
    private String id;
    @NotBlank(message = "host is required")
    private String host;
    @NotNull(message = "port is required")
    private Integer port;
    @NotBlank(message = "db is required")
    private String db;
    @NotBlank(message = "username is required")
    private String username;
    @NotBlank(message = "password is required")
    private String password;
    @NotBlank(message = "state is required")
    private String state;
    @NotNull(message = "mail server is required")
    private MailServerDto mailServer;


}
