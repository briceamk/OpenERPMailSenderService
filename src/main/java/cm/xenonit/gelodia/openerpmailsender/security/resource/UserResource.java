package cm.xenonit.gelodia.openerpmailsender.security.resource;

import cm.xenonit.gelodia.openerpmailsender.common.CommonUtils;
import cm.xenonit.gelodia.openerpmailsender.security.domain.User;
import cm.xenonit.gelodia.openerpmailsender.security.exception.UserUnauthorizedException;
import cm.xenonit.gelodia.openerpmailsender.security.generated.resource.UsersApi;
import cm.xenonit.gelodia.openerpmailsender.security.generated.resource.dto.*;
import cm.xenonit.gelodia.openerpmailsender.security.resource.dto.UserDto;
import cm.xenonit.gelodia.openerpmailsender.security.security.CustomJwtTokenProvider;
import cm.xenonit.gelodia.openerpmailsender.security.security.CustomUserDetails;
import cm.xenonit.gelodia.openerpmailsender.security.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import static cm.xenonit.gelodia.openerpmailsender.security.constant.SecurityConstant.TOKEN_PREFIX;
import static cm.xenonit.gelodia.openerpmailsender.security.resource.mapper.RoleMapper.fromRoles;
import static cm.xenonit.gelodia.openerpmailsender.security.resource.mapper.UserMapper.fromRegisterNewUserDto;
import static cm.xenonit.gelodia.openerpmailsender.security.resource.mapper.UserMapper.fromUser;
import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */

@RestController
@RequiredArgsConstructor
public class UserResource implements UsersApi {

    private final UserService userService;
    private final CustomJwtTokenProvider tokenProvider;
    private final HttpServletRequest request;

    @Override
    public ResponseEntity<ApiSuccessResponseDto> activateAccount(String code) {
        userService.activateAccount(code);
        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .success(true)
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.name())
                                .message("Your account is activated now. Please click the link below to be redirect at login page.")
                );
    }

    @Override
    @PreAuthorize("hasAuthority('read:user')")
    public ResponseEntity<ApiSuccessResponseDto> fetchUserById(String userId) {
        User user = userService.fetchById(userId);
        return ResponseEntity.status(OK).body(
                new ApiSuccessResponseDto()
                        .success(true)
                        .code(OK.value())
                        .status(OK.name())
                        .timestamp(now().toString())
                        .message("A new jwt access token was generated")
                        .data(of(
                                "user", fromUser(user), "roles", fromRoles(user.getRoles())
                        ))
        );
    }

    @Override
    public ResponseEntity<ApiSuccessResponseDto> login(LoginDto loginDto) {
        User user = userService.login(loginDto.getEmail(), loginDto.getPassword());

        return user.getUseMfa()
                ? sendVerificationCode(user)
                : sendSuccessLoginResponse(user);
    }

    @Override
    public ResponseEntity<ApiSuccessResponseDto> refreshToken() {
        if(tokenProvider.isHeaderTokenValid(request)) {
            String refreshToken = request.getHeader(AUTHORIZATION).substring(TOKEN_PREFIX.length());
            User user = userService.fetchById(tokenProvider.getSubject(refreshToken, request));
            return ResponseEntity.status(OK).body(
                    new ApiSuccessResponseDto()
                            .success(true)
                            .code(OK.value())
                            .status(OK.name())
                            .timestamp(now().toString())
                            .message("A new jwt access token was generated")
                            .data(of(
                                    "user", fromUser(user),
                                    "accessToken", tokenProvider.createJwtAccessToken(new CustomUserDetails(user)),
                                    "refreshToken", refreshToken
                            ))
            );

        }
        throw new UserUnauthorizedException("Refresh token missing or invalid");
    }
    @Override
    public ResponseEntity<ApiSuccessResponseDto> register(RegisterUserDto registerUserDto) {
        User user = fromRegisterNewUserDto(registerUserDto);
        UserDto userDto = fromUser(userService.registerUser(user));
        return ResponseEntity.status(CREATED)
                .body(
                        new ApiSuccessResponseDto()
                                .success(true)
                                .timestamp(now().toString())
                                .code(CREATED.value())
                                .status(CREATED.name())
                                .message("User register successfully. check your email " +
                                        "and click the link to validate your account.")
                                .data(of("user", userDto))
                );
    }

    @Override
    public ResponseEntity<ApiSuccessResponseDto> requestResetPassword(RequestResetPasswordDto requestResetPasswordDto) {
        userService.requestResetPassword(requestResetPasswordDto.getEmail());
        String email = CommonUtils.maskEmail(requestResetPasswordDto.getEmail());
        return ResponseEntity.status(CREATED)
                .body(
                        new ApiSuccessResponseDto()
                                .success(true)
                                .code(CREATED.value())
                                .status(CREATED.name())
                                .timestamp(now().toString())
                                .message(String.format("A verification url has send to %s. Please check your mail box click the link to reset your password.", email))
                );
    }

    @Override
    public ResponseEntity<ApiSuccessResponseDto> resendCode(String email) {
        User user = userService.resendVerificationCode(email);
        email = CommonUtils.maskEmail(email);
        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .success(true)
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.name())
                                .message(String.format("A verification code has send to %s. Please check your mail box and enter the code below.", email))
                                .data(of("user", fromUser(user)))

                );
    }

    @Override
    public ResponseEntity<ApiSuccessResponseDto> resetPassword(String code, ResetPassword resetPassword) {
        userService.resetPassword(code, resetPassword.getPassword(), resetPassword.getConfirmPassword());
        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .success(true)
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.name())
                                .message("Your password has changed. Please click the link below to login again")

                );
    }

    @Override
    public ResponseEntity<ApiSuccessResponseDto> verifyMfa(String email, String code) {
        return sendSuccessLoginResponse(userService.verifyCode(email, code));
    }

    private ResponseEntity<ApiSuccessResponseDto> sendSuccessLoginResponse(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);
        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .success(true)
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.name())
                                .message("User logged in.")
                                .data(
                                        of(
                                                "user", fromUser(user),
                                                "accessToken", tokenProvider.createJwtAccessToken(userDetails),
                                                "refreshToken", tokenProvider.createJwtRefreshToken(userDetails)
                                        )
                                )
                );
    }

    private ResponseEntity<ApiSuccessResponseDto> sendVerificationCode(User user) {
        userService.sendVerificationCode(user);
        String email = CommonUtils.maskEmail(user.getEmail());
        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .success(true)
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.name())
                                .message(String.format("A verification code has send to %s. Please check your mail box and enter the code below.", email))
                                .data(of("user", fromUser(user)))
                );
    }
}
