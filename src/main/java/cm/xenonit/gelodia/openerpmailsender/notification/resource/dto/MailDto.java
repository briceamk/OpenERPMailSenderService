package cm.xenonit.gelodia.openerpmailsender.notification.resource.dto;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailServer;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailTemplate;
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
public class MailDto {
    @NotBlank(message = "id is required")
    private String id;
    @NotBlank(message = "recipient is required")
    private String to;
    @NotBlank(message = "subject is required")
    private String subject;
    private String message;
    private Integer attemptToSend;
    private Long externalId;
    private Long externalServerId;
    @NotBlank(message = "state is required")
    private String state;
    @NotBlank(message = "type is required")
    private String type;
    @NotNull(message = "create date is required")
    private LocalDateTime createdAt;
    private LocalDateTime sendAt;
    @NotNull(message = "mail server is required")
    private MailServer mailServer;
    @NotNull(message = "mail template is required")
    private MailTemplate mailTemplate;
}
