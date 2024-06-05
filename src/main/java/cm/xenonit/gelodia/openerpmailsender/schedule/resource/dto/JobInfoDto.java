package cm.xenonit.gelodia.openerpmailsender.schedule.resource.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * @author bamk
 * @version 1.0
 * @since 17/02/2024
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobInfoDto {
    @NotBlank(message = "Jid is required")
    private String id;
    @NotBlank(message = "Job name is required")
    private String jobName;
    @NotBlank(message = "Job group is required")
    private String jobGroup;
    @NotBlank(message = "Job class is required")
    private String jobClass;
    private String cronExpression;
    private Boolean useAsCronJob;
    private Long interval;
    @NotBlank(message = "Job state class is required")
    private String state;
}
