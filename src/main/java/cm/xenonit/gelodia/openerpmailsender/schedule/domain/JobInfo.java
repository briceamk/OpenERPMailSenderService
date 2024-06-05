package cm.xenonit.gelodia.openerpmailsender.schedule.domain;

import cm.xenonit.gelodia.openerpmailsender.schedule.exception.JobInfoBadRequestException;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.UuidGenerator;

/**
 * @author bamk
 * @version 1.0
 * @since 17/02/2024
 */
@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_job_info")
public class JobInfo {
    @Id
    @UuidGenerator
    @Column(name = "c_id", unique = true, nullable = false)
    private String id;
    @Column(name = "c_job_name", nullable = false, unique = true)
    private String jobName;
    @Column(name = "c_job_group", nullable = false)
    private String jobGroup;
    @Column(name = "c_job_class", nullable = false, unique = true)
    private String jobClass;
    @Column(name = "c_cron_expression")
    private String cronExpression;
    @Column(name = "c_use_as_cron_job")
    private Boolean useAsCronJob;
    @Column(name = "c_interval")
    private Long interval;
    @Enumerated(EnumType.STRING)
    @Column(name = "c_state", nullable = false)
    private JobInfoState state;

    public void validate() {
        if(!this.useAsCronJob && this.interval == null) {
            throw new JobInfoBadRequestException("If job is not cron, you need to provide an interval");
        }
        if(this.useAsCronJob && StringUtils.isBlank(this.cronExpression)) {
            throw new JobInfoBadRequestException("When job is cron, you need to provide a cron expression");
        }
        //TODO validate cron expression with a regular expression
    }

    public void initialize() {
        this.setState(JobInfoState.DRAFT);
    }

    public void scheduled() {
        this.setState(JobInfoState.SCHEDULED);
    }

    public void unscheduled() {
        this.setState(JobInfoState.UNSCHEDULED);
    }

    public void resume() {
        this.setState(JobInfoState.PAUSED);
    }

    public void reset() {
        this.setState(JobInfoState.DRAFT);
    }
}
