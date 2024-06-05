package cm.xenonit.gelodia.openerpmailsender.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * @author bamk
 * @version 1.0
 * @since 24/01/2024
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(NON_NULL)
public class ApiResponseDto {
    @NotBlank(message = "timestamp is required")
    private String timestamp;
    @NotNull(message = "code is required")
    private Integer code;
    @NotBlank(message = "status is required")
    private String status;
    @NotBlank(message = "success is required")
    private Boolean success;


    public ApiResponseDto(String timestamp, Integer code, String status, Boolean success) {
        this.timestamp = timestamp;
        this.code = code;
        this.status = status;
        this.success = success;
    }
}
