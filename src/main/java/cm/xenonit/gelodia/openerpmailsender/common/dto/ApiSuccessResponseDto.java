package cm.xenonit.gelodia.openerpmailsender.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

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
public class ApiSuccessResponseDto extends ApiResponseDto{
    private String message;
    private Map<String, ?> data;

    @Builder
    public ApiSuccessResponseDto(String timestamp, Integer code, String status, Boolean success, String message,
                                 Map<String, ?> data) {
        super(timestamp, code, status, success);
        this.message = message;
        this.data = data;
    }
}
