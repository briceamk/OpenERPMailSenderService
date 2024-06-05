package cm.xenonit.gelodia.openerpmailsender.security.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
@Getter
@RequiredArgsConstructor
public enum VerificationType {
    Account("activate-account"), Password("reset-password"), Mfa("mfa");

    private final String type;
}
