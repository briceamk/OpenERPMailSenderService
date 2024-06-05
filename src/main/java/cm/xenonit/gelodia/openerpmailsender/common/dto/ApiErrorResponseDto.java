package cm.xenonit.gelodia.openerpmailsender.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
public class ApiErrorResponseDto extends ApiResponseDto{
    private String reason;
    private String developerMessage;
    private String path;
    private List<ValidationErrorDto> error;

    @Builder
    public ApiErrorResponseDto(String timestamp, Integer code, String status, Boolean success, String reason,
                               String developerMessage, String path, List<ValidationErrorDto> error) {
        super(timestamp, code, status, success);
        this.reason = reason;
        this.developerMessage = developerMessage;
        this.path = path;
        this.error = error;
    }
}
