package cm.xenonit.gelodia.openerpmailsender.notification.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author bamk
 * @version 1.0
 * @since 24/01/2024
 */
@Getter
@AllArgsConstructor
public enum MailState {
    DRAFT("Queued"), SENDING("Waiting"), SEND("Sent"), ERROR("Error"), COMPLETE("Complete");
    private final String state;
}
