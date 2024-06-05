package cm.xenonit.gelodia.openerpmailsender.security.service.implementation;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailTemplateType;
import cm.xenonit.gelodia.openerpmailsender.security.domain.Role;
import cm.xenonit.gelodia.openerpmailsender.security.domain.User;
import cm.xenonit.gelodia.openerpmailsender.security.domain.Verification;
import cm.xenonit.gelodia.openerpmailsender.security.domain.enums.VerificationType;
import cm.xenonit.gelodia.openerpmailsender.security.event.ActivateAccountNotificationEvent;
import cm.xenonit.gelodia.openerpmailsender.security.event.ResetPasswordNotificationEvent;
import cm.xenonit.gelodia.openerpmailsender.security.event.SendMfaNotificationEvent;
import cm.xenonit.gelodia.openerpmailsender.security.exception.*;
import cm.xenonit.gelodia.openerpmailsender.security.repository.UserRepository;
import cm.xenonit.gelodia.openerpmailsender.security.repository.VerificationRepository;
import cm.xenonit.gelodia.openerpmailsender.security.security.CustomUserDetails;
import cm.xenonit.gelodia.openerpmailsender.security.service.RoleService;
import cm.xenonit.gelodia.openerpmailsender.security.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;

import static cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailTemplateType.ACTIVATE_ACCOUNT;
import static cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailTemplateType.RESET_PASSWORD;
import static cm.xenonit.gelodia.openerpmailsender.security.constant.SecurityConstant.*;
import static cm.xenonit.gelodia.openerpmailsender.security.domain.enums.RoleType.ROLE_MANAGER;
import static cm.xenonit.gelodia.openerpmailsender.security.domain.enums.VerificationStatus.NotVerified;
import static cm.xenonit.gelodia.openerpmailsender.security.domain.enums.VerificationStatus.Verified;
import static cm.xenonit.gelodia.openerpmailsender.security.domain.enums.VerificationType.*;
import static java.time.LocalDateTime.now;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {


    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher publisher;

    @Value("${security.max.failed.login.count}")
    private int maxFailedLogin;
    @Value("${frontend.context.host}")
    private String frontEndContextHost;

    @Override
    @Transactional
    public User registerUser(User user) {
        log.info("Attempt to register a new user in database");
        if(userRepository.countByEmailIgnoreCase(user.getEmail()) > 0) {
            throw new UserConflictException("Mail already in use. Please use a different email and try again.");
        }
        user.verifyPassword();
        user.initialize();
        user.cryptPassword(encryptPassword(user.getPassword()));
        Role role = roleService.findRoleType(ROLE_MANAGER);
        user.addRole(role);
        user = userRepository.save(user);
        String verificationUrl = getVerificationUrl(randomAlphanumeric(64), Account.getType());
        saveVerificationUrl(user, verificationUrl, Account);
        log.info("User registered in database successfully.");
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public User fetchByEmail(String email) {
        log.info("Attempt to find user by email");
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()) {
            throw new UserNotFoundException("User with email not found. Please try again.");
        } else {
            log.info("User found in database successfully.");
            return optionalUser.get();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User login(String email, String password) {
        log.info("Attempt to login user.");
        if (isBruteForceAttack(email)) {
            //We check if account is in brute force attacks
            throw new UserBadRequestException(
                    String.format("User account is currently locked due to %s failed login attempts", maxFailedLogin)
            );
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        log.info("User logged in successfully.");
        return ((CustomUserDetails) authentication.getPrincipal()).getUser();

    }

    @Override
    @Transactional
    public void activateAccount(String verificationCode) {
        String verificationUrl = getVerificationUrl(verificationCode, Account.getType());
        Optional<Verification> optionalVerification = verificationRepository.findByUrl(verificationUrl);
    Verification verification = validateUrlVerification(optionalVerification, verificationUrl, Account);
        verification.validate();
        verification.getUser().enableAccount();
        userRepository.save(verification.getUser());
        verificationRepository.save(verification);
    }

    @Override
    @Transactional
    public void sendVerificationCode(User user) {
        log.info("Attempt to send verification code to user with mfa");

        verificationRepository.findByUserAndStatus(user, NotVerified)
            .ifPresent(verification -> {
            log.info("Cancelling old verification code");
            verification.cancelled();
            verificationRepository.save(verification);
        });

        String code = randomNumeric(6);
        Verification verification = new Verification();
        verification.initialize(user, code, Mfa);
        verificationRepository.save(verification);
        publishMfaNotificationEvent(user, code);
    }

    @Override
    @Transactional
    public User verifyCode(String email, String code) {
        User user = fetchByEmail(email);
        Optional<Verification> optionalVerification = verificationRepository.findByUserAndCode(user, code);
        Verification verification = validateCodeVerification(optionalVerification, code);
        verification.validate();
        verificationRepository.save(verification);
        return user;
    }

    @Override
    @Transactional
    public void registerLoginFailure(String email) {
        User user = fetchByEmail(email);
        if(user.getAccountEnabled()) {
            int failedCounter = user.getFailedLoginAttempt() + 1;
            if(maxFailedLogin <= failedCounter) {
                user.setFailedLoginAttempt(failedCounter);
                user.disableAccount();
            } else {
                user.setFailedLoginAttempt(failedCounter);
            }
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void resetBruteForceCounter(String email) {
        User user = fetchByEmail(email);
        user.resetFailedLoginAttempt();
        user.enableAccount();
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBruteForceAttack(String email) {
        User user = fetchByEmail(email);
        return !user.getAccountEnabled() && user.getFailedLoginAttempt() >= maxFailedLogin;
    }

    @Override
    @Transactional
    public User resendVerificationCode(String email) {
        log.info("Attempt to resend verification code");
        User user = fetchByEmail(email);
        sendVerificationCode(user);
        return user;
    }

    @Override
    @Transactional
    public void requestResetPassword(String email) {
        User user = fetchByEmail(email);
        String verificationUrl = getVerificationUrl(randomAlphanumeric(64), Password.getType());
        saveVerificationUrl(user, verificationUrl, Password);
    }

    @Override
    public void resetPassword(String code, String password, String confirmPassword) {
        String verificationUrl = getVerificationUrl(code, Password.getType());
        Optional<Verification> optionalVerification = verificationRepository.findByUrl(verificationUrl);
        Verification verification = validateUrlVerification(optionalVerification, verificationUrl, Password);
        User user = verification.getUser();
        user.verifyPassword(password, confirmPassword);
        user.cryptPassword(encryptPassword(password));
        userRepository.save(user);
        verification.validate();
        verificationRepository.save(verification);
    }

    @Override
    @Transactional(readOnly = true)
    public User fetchById(String id) {
        log.info("Attempt to find user by id");
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isEmpty()) {
            throw new UserNotFoundException("User with id not found. Please try again.");
        } else {
            log.info("User found in database successfully.");
            return optionalUser.get();
        }
    }

    private String encryptPassword(String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    private String getVerificationUrl(String code, String type) {
        return ServletUriComponentsBuilder
                .fromHttpUrl(frontEndContextHost)
                .path(String.format("/%s/%s", type, code))
                .toUriString();
    }

    private void saveVerificationUrl(User user, String verificationUrl, VerificationType type) {
        log.info("Attempt to save verification code");

        verificationRepository.findByUserAndStatus(user, NotVerified)
            .ifPresent(verification -> {
                log.info("Cancelling old verification code");
                verification.cancelled();
                verificationRepository.save(verification);
            });

        Verification verification = new Verification();
        verification.initialize(user, verificationUrl, type);
        verificationRepository.save(verification);
        publishActivateAccountOrResetPasswordNotificationEvent(user, verificationUrl, type);
    }

    private Verification validateUrlVerification(Optional<Verification> optionalVerification, String verificationUrl, VerificationType type) {
        if(optionalVerification.isEmpty()) {
            String message = type.equals(Password)
                    ? "The link is not found. Back to login page and reset your password again"
                    : "The link is not found. Create a new account and try to validate again.";

            throw new VerificationNotFoundException(message);
        }
        Verification verification = optionalVerification.get();
        if(verification.getStatus() != null && verification.getStatus().equals(Verified)) {
            String message = type.equals(Password)
                    ? "The password is already reset. If you want to reset again, Back to login page click the reset link."
                    : "The account is already verified.";
            throw new VerificationBadRequestException(message);
        }
        if(!(StringUtils.isNoneEmpty(verification.getUrl()) && verification.getUrl().equals(verificationUrl))) {
            throw new VerificationBadRequestException("The link is not valid!");
        }
        if(verification.getExpiration() != null && verification.getExpiration().isBefore(now())) {
            String message = type.equals(Password)
                    ? "The link is expired. Back to login page and reset your password again"
                    : "The link is expired. Create a new account and try to validate again.";
            throw new VerificationBadRequestException(message);
        }
        return optionalVerification.get();
    }

    private Verification validateCodeVerification(Optional<Verification> optionalVerification, String code) {
        if(optionalVerification.isEmpty()) {
            throw new VerificationNotFoundException("Mfa code not found. Please try again!");
        }
        Verification verification = optionalVerification.get();
        if(verification.getStatus() != null && verification.getStatus().equals(Verified)) {
            throw new VerificationBadRequestException("The mfa code is already used. Please login again.");
        }
        if(!(StringUtils.isNoneEmpty(verification.getCode()) && verification.getCode().equals(code))) {
            throw new VerificationBadRequestException("The link is not valid!");
        }
        if(verification.getExpiration() != null && verification.getExpiration().isBefore(now())) {
            throw new VerificationBadRequestException("The mfa code is expired. Please login again!");
        }
        return optionalVerification.get();
    }

    private void publishActivateAccountOrResetPasswordNotificationEvent(User user, String verificationUrl, VerificationType type) {
        String fullName = StringUtils.isNotBlank(user.getFirstName())
                ? String.format("%s %s", user.getFirstName(), user.getLastName())
                : user.getLastName();

        String subject = type.equals(Account) ? EMAIL_ACTIVATION_ACCOUNT_SUBJECT : EMAIL_RESET_PASSWORD_SUBJECT;

        MailTemplateType templateType = type.equals(Account) ? ACTIVATE_ACCOUNT : RESET_PASSWORD;

        String expireIn = type.equals(Account)
                ? String.valueOf(DEFAULT_ACCOUNT_EXPIRATION_IN_MINUTES / 1440)
                : String.valueOf(DEFAULT_PASSWORD_EXPIRATION_IN_MINUTES);

        if (type.equals(Account))
            publisher.publishEvent(
                    new ActivateAccountNotificationEvent(user.getEmail(), fullName, verificationUrl, expireIn, templateType, subject));
        else
            publisher.publishEvent(
                    new ResetPasswordNotificationEvent(user.getEmail(), fullName, verificationUrl, expireIn, templateType, subject));


    }

    private void publishMfaNotificationEvent(User user, String code) {
        String fullName = StringUtils.isNotBlank(user.getFirstName())
                ? String.format("%s %s", user.getFirstName(), user.getLastName())
                : user.getLastName();

        String expireIn = String.valueOf(DEFAULT_MFA_EXPIRATION_IN_MINUTES);

        publisher.publishEvent(
                new SendMfaNotificationEvent(user.getEmail(), fullName, code, expireIn, MailTemplateType.MFA, EMAIL_MFA_SUBJECT));


    }

}
