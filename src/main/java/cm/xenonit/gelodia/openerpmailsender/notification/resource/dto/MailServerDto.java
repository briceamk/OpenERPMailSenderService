package cm.xenonit.gelodia.openerpmailsender.notification.resource.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * @author bamk
 * @version 1.0
 * @since 24/01/2024
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailServerDto {
    @NotBlank(message = "id is required.")
    private String id;
    @NotBlank(message = "name is required.")
    private String name;
    @NotBlank(message = "Mail is required. Please provide one and try again.")
    @Email(message = "Please provide a valid email.")
    private String fromEmail;
    @NotBlank(message = "Server type is required. Please select one.")
    private String type;
    @NotBlank(message = "Host is required.")
    private String host;
    @NotNull(message = "Port is required.")
    @Min(value = 0, message = "The value of a port must not be less than 0.")
    @Max(value = 65535, message = "The value of a port must not be above zeros 65535.")
    private Integer port;
    @NotBlank(message = "Protocol is required.")
    private String protocol;
    private Boolean useSSL;
    private Boolean useAuth;
    private Boolean useAsDefault;
    @Email(message = "Please provide a valid email.")
    private String username;
    private String password;
    @NotBlank(message = "Server state is required.")
    private String state;
    private LocalDate createdAt;
}
