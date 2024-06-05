package cm.xenonit.gelodia.openerpmailsender.common;

import org.apache.commons.lang3.StringUtils;

import static cm.xenonit.gelodia.openerpmailsender.common.constant.CommonConstant.MASK;

/**
 * @author bamk
 * @version 1.0
 * @since 17/02/2024
 */
public class CommonUtils {
    public static String maskEmail(String something) {
        String prefix = something.split("@")[0];
        String suffix = something.split("@")[1];
        prefix = StringUtils.overlay(prefix, StringUtils.repeat(
                MASK, prefix.length()/2), (prefix.length()/2) - 1, prefix.length());
        suffix = StringUtils.overlay(suffix, StringUtils.repeat(
                MASK, suffix.length()/2), 0, suffix.length()/2);
        return String.format("%s@%s", prefix, suffix);
    }
}
