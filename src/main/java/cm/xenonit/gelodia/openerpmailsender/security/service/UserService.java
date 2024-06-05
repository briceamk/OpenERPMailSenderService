package cm.xenonit.gelodia.openerpmailsender.security.service;

import cm.xenonit.gelodia.openerpmailsender.security.domain.User;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
public interface UserService {

    User registerUser(User user);

    User fetchByEmail(String email);

    User login(String email, String password);

    void activateAccount(String verificationCode);

    void sendVerificationCode(User user);

    User verifyCode(String email, String code);

    void registerLoginFailure(String email);
    void resetBruteForceCounter(String email);

    boolean isBruteForceAttack(String email);

    User resendVerificationCode(String email);

    void requestResetPassword(String email);

    void resetPassword(String code, String password, String confirmPassword);

    User fetchById(String id);
}
