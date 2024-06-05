package cm.xenonit.gelodia.openerpmailsender.schedule.service;

import cm.xenonit.gelodia.openerpmailsender.common.enums.SortDirection;
import cm.xenonit.gelodia.openerpmailsender.schedule.domain.JobInfo;
import org.springframework.data.domain.Page;

/**
 * @author bamk
 * @version 1.0
 * @since 17/02/2024
 */
public interface JobInfoService {

    JobInfo createJobInfo(JobInfo jobInfo);

    JobInfo findJobById(String id);

    JobInfo scheduledJob(String id);
    JobInfo unScheduledJob(String id);
    JobInfo startJob(String id);
    JobInfo resumeJob(String id);
    JobInfo deleteJob(String id);
    JobInfo reScheduleJob(String id);

    void startAllSchedulers();

    Page<JobInfo> fetchJobInfoByKeyword(Integer page, Integer size, SortDirection sortDirection, String attribute, String keyword);

    Page<JobInfo> fetchJobInfos(Integer page, Integer size, SortDirection sortDirection, String attribute);
}
