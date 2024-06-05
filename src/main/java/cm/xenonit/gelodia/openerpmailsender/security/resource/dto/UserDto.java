package cm.xenonit.gelodia.openerpmailsender.security.resource.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @NotBlank(message = "id is required.")
    private String id;
    private String firstName;
    @NotBlank(message = "lastname is required. Please enter your lastname and try again.")
    private String lastName;
    @NotBlank(message = "lastname is required. Please enter your lastname and try again.")
    @Email(message = "invalid email. Please enter a valid email and try again.")
    private String email;
    private String phone;
    @NotBlank(message = "image url is required.")
    private String imageUrl;
    private Boolean accountEnabled;
    private Boolean accountNotLocked;
    private Boolean credentialsNotExpired;
    private Boolean useMfa;
    private LocalDateTime createdAt;
    @NotBlank(message = "role name is required.")
    private String roleName;
    @NotBlank(message = "permissions is required.")
    private String permissions;
}
