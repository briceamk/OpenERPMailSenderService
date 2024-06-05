package cm.xenonit.gelodia.openerpmailsender.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * @author bamk
 * @version 1.0
 * @since 05/02/2024
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorDto {
    @NotBlank(message = "field is required")
    private String field;
    @NotBlank(message = "message is required")
    private String message;
}
