package cm.xenonit.gelodia.openerpmailsender.security.constant;

import static cm.xenonit.gelodia.openerpmailsender.common.constant.CommonConstant.API_ROOT_URL;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
public class SecurityConstant {

    //User
    public static final String DEFAULT_IMAGE_URL = "https://cdn-icons-png.flaticon.com/512/149/149071.png";

    //Verification
    public static final int DEFAULT_MFA_EXPIRATION_IN_MINUTES = 5;
    public static final int DEFAULT_PASSWORD_EXPIRATION_IN_MINUTES = 10;
    public static final int DEFAULT_ACCOUNT_EXPIRATION_IN_MINUTES = 7200;

    //UserResource
    public static final String API_USER_ROOT_URL = API_ROOT_URL + "/users";

    //Config
    public static final int STRENGTH = 12;
    public static final String[] PUBLIC_URLS = {
            API_USER_ROOT_URL + "/register/**",
            API_USER_ROOT_URL + "/login/**",
            API_USER_ROOT_URL + "/activate/account/**",
            API_USER_ROOT_URL + "/verify/mfa/**",
            API_USER_ROOT_URL + "/resend/mfa/code/**",
            API_USER_ROOT_URL + "/verify/password/**",
            API_USER_ROOT_URL + "/reset-password/request-link/**",
            API_USER_ROOT_URL + "/refresh/token/**",
            "/swagger-ui/**"
    };

    //CustomAuthorizationFilter
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String EMPTY = "";
    public static final String TOKEN = "token";
    public static final String USER_ID_KEY = "id";
    public static final String[] PUBLIC_URLS_TO_NOT_FILTER = {
             API_USER_ROOT_URL + "/register",
             API_USER_ROOT_URL + "/login",
             API_USER_ROOT_URL + "/activate/account",
             API_USER_ROOT_URL + "/verify/mfa",
            API_USER_ROOT_URL + "/resend/mfa/code",
            API_USER_ROOT_URL + "/verify/password",
            API_USER_ROOT_URL + "/reset-password/request-link",
            API_USER_ROOT_URL + "/refresh/token",
            "/swagger-ui/index.html"
    };
    public static final String HTTP_OPTIONS_METHOD_TO_NOT_FILTER = "OPTIONS";

    //JwtTokenProvider
    public static final String XENON_BYTE_SARL = "XENON BYTE SARL";
    public static final String OPENERP_EMAIL_SENDER = "OpenERP Mail Sender";
    public static final String AUTHORITIES = "authorities";

    //CustomUserDetails
    public static final String COMMA_SEPARATOR = ",";

    //UserServiceImplementation
    public static final String EMAIL_ACTIVATION_ACCOUNT_SUBJECT = "EmailSenderAPP: Activez votre compte!";
    public static final String EMAIL_RESET_PASSWORD_SUBJECT = "EmailSenderAPP: Créez un nouveau mot de passe!";
    public static final String EMAIL_MFA_SUBJECT = "EmailSenderAPP: Votre code pour vous connecter!";
}
