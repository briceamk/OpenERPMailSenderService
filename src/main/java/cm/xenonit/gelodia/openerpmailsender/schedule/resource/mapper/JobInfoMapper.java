package cm.xenonit.gelodia.openerpmailsender.schedule.resource.mapper;

import cm.xenonit.gelodia.openerpmailsender.schedule.domain.JobInfo;
import cm.xenonit.gelodia.openerpmailsender.schedule.generated.resource.dto.CreateJobInfoRequestDto;
import cm.xenonit.gelodia.openerpmailsender.schedule.resource.dto.JobInfoDto;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author bamk
 * @version 1.0
 * @since 17/02/2024
 */
@Component
public class JobInfoMapper {

    public JobInfo fromCreateJobInfoRequestDto(CreateJobInfoRequestDto jobInfoRequestDto) {
        JobInfo jobInfo = new JobInfo();
        BeanUtils.copyProperties(jobInfoRequestDto, jobInfo);
        return jobInfo;
    }

    public JobInfoDto fromJobInfo(JobInfo jobInfo) {
        JobInfoDto jobInfoDto = new JobInfoDto();
        BeanUtils.copyProperties(jobInfo, jobInfoDto);
        jobInfoDto.setState(jobInfo.getState().name());
        return jobInfoDto;
    }

    public List<JobInfoDto> fromJobInfoPage(Page<JobInfo> jobInfoPage) {
        return jobInfoPage.getContent().stream().map(this::fromJobInfo).toList();
    }
}
