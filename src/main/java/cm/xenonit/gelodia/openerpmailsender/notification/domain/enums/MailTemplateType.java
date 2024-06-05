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
public enum MailTemplateType {

    ACTIVATE_ACCOUNT("Activate Account"),
    RESET_PASSWORD("Reset Password"),
    MFA("Mfa"),
    VERIFY_MAIL_SERVER("Verify MailServer"),
    NOT_APPLICABLE("Not Applicable");

    private final String type;
}
