package cm.xenonit.gelodia.openerpmailsender.notification.resource.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author bamk
 * @version 1.0
 * @since 19/02/2024
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MailHistoryDto {
    @NotBlank(message = "id is required")
    private String id;
    @NotBlank(message = "recipient is required")
    private String to;
    @NotBlank(message = "subject is required")
    private String subject;
    @NotBlank(message = "subject is required")
    private String message;
    @NotNull(message = "create date is required")
    private LocalDateTime createdAt;
    @NotNull(message = "subject is required")
    private MailServerDto mailServer;
}
