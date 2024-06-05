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
public enum MailServerState {
    NEW("Not Verified"),
    WAITING("Waiting for verifcation"),
    CONFIRM("Verified");

    private final String type;
}
