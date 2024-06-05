package cm.xenonit.gelodia.openerpmailsender.schedule.service.implementation;

import cm.xenonit.gelodia.openerpmailsender.common.enums.SortDirection;
import cm.xenonit.gelodia.openerpmailsender.schedule.domain.JobInfo;
import cm.xenonit.gelodia.openerpmailsender.schedule.domain.JobInfoState;
import cm.xenonit.gelodia.openerpmailsender.schedule.exception.JobInfoBadRequestException;
import cm.xenonit.gelodia.openerpmailsender.schedule.exception.JobInfoConflictException;
import cm.xenonit.gelodia.openerpmailsender.schedule.exception.JobInfoNotFoundException;
import cm.xenonit.gelodia.openerpmailsender.schedule.exception.JobInfoTechnicalException;
import cm.xenonit.gelodia.openerpmailsender.schedule.repository.JobInfoRepository;
import cm.xenonit.gelodia.openerpmailsender.schedule.service.JobInfoService;
import cm.xenonit.gelodia.openerpmailsender.schedule.util.JobInfoCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author bamk
 * @version 1.0
 * @since 17/02/2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobInfoServiceImplementation implements JobInfoService {

    private final JobInfoRepository jobInfoRepository;
    private final SchedulerFactoryBean schedulerFactoryBean;
    private final ApplicationContext context;
    private final JobInfoCreator jobInfoCreator;

    @Override
    @Transactional
    public JobInfo createJobInfo(JobInfo jobInfo) {
        jobInfo.validate();
        checkDuplicateJobInfo(jobInfo);
        jobInfo.initialize();
        return jobInfoRepository.save(jobInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public JobInfo findJobById(String id) {
        return jobInfoRepository.findById(id).orElseThrow(
                () -> new JobInfoNotFoundException(String.format("Job with id '%s' not found in database.", id))
        );
    }

    @Override
    @Transactional
    public JobInfo scheduledJob(String id) {
        JobInfo jobInfo = findJobById(id);
        try{
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            JobDetail jobDetail = JobBuilder.newJob((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()))
                    .withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup()).build();

            if(!scheduler.checkExists(jobDetail.getKey())) {
                Trigger trigger;
                JobDetail job = jobInfoCreator.createJob(
                        (Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()),
                        false,
                        context,
                        jobInfo.getJobName(),
                        jobInfo.getJobGroup()
                );
                if(jobInfo.getUseAsCronJob() && CronExpression.isValidExpression(jobInfo.getCronExpression())) {
                    trigger = jobInfoCreator.createCronTrigger(
                            jobInfo.getJobName(),
                            new Date(),
                            jobInfo.getCronExpression(),
                            SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW
                    );
                } else {
                    trigger = jobInfoCreator.createIntervalTrigger(
                            jobInfo.getJobName(),
                            new Date(),
                            jobInfo.getInterval(),
                            SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW
                    );
                }
                scheduler.scheduleJob(jobDetail, trigger);
                jobInfo.scheduled();
                return jobInfoRepository.save(jobInfo);
            }
            return jobInfo;
        } catch (ClassNotFoundException exception) {
            throw new JobInfoNotFoundException(String.format("Class with name %s not found", jobInfo.getJobClass()));
        } catch (SchedulerException exception) {
            throw new JobInfoTechnicalException(String.format("Unable to schedule th job with name %s", jobInfo.getJobName()));
        }
    }

    @Override
    @Transactional
    public JobInfo unScheduledJob(String id) {
        JobInfo jobInfo = findJobById(id);
        if(!jobInfo.getState().equals(JobInfoState.SCHEDULED)) {
            throw  new JobInfoBadRequestException(
                String.format(
                        "Job is not scheduled. Please scheduled job first before attempts to un-scheduled",
                        jobInfo.getJobName())
            );
        }
        try {
            boolean isUnScheduled = schedulerFactoryBean.getScheduler().unscheduleJob(new TriggerKey(jobInfo.getJobName()));
            if(isUnScheduled) {
                jobInfo.unscheduled();
                jobInfoRepository.save(jobInfo);
            }
            return jobInfo;
        } catch (SchedulerException exception) {
            throw new JobInfoTechnicalException(
                    String.format("Unable to un-schedule job with id name '%s'", jobInfo.getJobName()));
        }
    }

    @Override
    @Transactional
    public JobInfo startJob(String id) {
        JobInfo jobInfo = findJobById(id);
        if(!jobInfo.getState().equals(JobInfoState.SCHEDULED)) {
            throw  new JobInfoBadRequestException(
                    String.format(
                            "Job is not scheduled. Please scheduled job first before attempts to start",
                            jobInfo.getJobName())
            );
        }

        try {
            schedulerFactoryBean.getScheduler().triggerJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
            jobInfo.scheduled();
            return jobInfoRepository.save(jobInfo);
        } catch (SchedulerException exception) {
            throw new JobInfoTechnicalException(
                    String.format("Unable to start job with id name '%s'", jobInfo.getJobName()));
        }
    }

    @Override
    @Transactional
    public JobInfo resumeJob(String id) {
        JobInfo jobInfo = findJobById(id);
        if(!jobInfo.getState().equals(JobInfoState.PAUSED)) {
            throw  new JobInfoBadRequestException(
                    String.format(
                            "Job '%s' is not in pause. Please pause job first before attempts to resume",
                            jobInfo.getJobName())
            );
        }
        try {
            schedulerFactoryBean.getScheduler().resumeJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
            jobInfo.scheduled();
            return jobInfoRepository.save(jobInfo);
        } catch (SchedulerException exception) {
            throw new JobInfoTechnicalException(
                    String.format("Unable to resume job with id name '%s'", jobInfo.getJobName()));
        }
    }

    @Override
    @Transactional
    public JobInfo deleteJob(String id) {
        JobInfo jobInfo = findJobById(id);
        try {
            schedulerFactoryBean.getScheduler().deleteJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
            jobInfo.reset();
            return jobInfoRepository.save(jobInfo);
        } catch (SchedulerException exception) {
            throw new JobInfoTechnicalException(
                    String.format("Unable to delete job with id name '%s'", jobInfo.getJobName()));
        }
    }

    @Override
    @Transactional
    public JobInfo reScheduleJob(String id) {
        JobInfo jobInfo = findJobById(id);
        if(jobInfo.getState().equals(JobInfoState.DRAFT)) {
            throw  new JobInfoBadRequestException(
                    String.format(
                            "Job '%s' is draft. You can't re-scheduled a job which is not scheduled yet.",
                            jobInfo.getJobName())
            );
        }
        Trigger trigger;
        if(jobInfo.getUseAsCronJob()) {
            trigger = jobInfoCreator.createCronTrigger(
                    jobInfo.getJobName(),
                    new Date(),
                    jobInfo.getCronExpression(),
                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW
            );
        } else {
            trigger = jobInfoCreator.createIntervalTrigger(
                    jobInfo.getJobName(),
                    new Date(),
                    jobInfo.getInterval(),
                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW
            );
        }
        try {
            schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobInfo.getJobName()), trigger);
            jobInfo.scheduled();
            return jobInfoRepository.save(jobInfo);
        } catch (SchedulerException exception) {
            throw new JobInfoTechnicalException(
                    String.format("Unable to delete job with id name '%s'", jobInfo.getJobName()));
        }
    }

    @Override
    @Transactional
    public void startAllSchedulers() {
        jobInfoRepository.findAll().forEach(jobInfo -> deleteJob(jobInfo.getId()));
    }

    @Override
    public Page<JobInfo> fetchJobInfoByKeyword(Integer page, Integer size, SortDirection sortDirection, String attribute, String keyword) {
        Sort.Direction direction = sortDirection.equals(SortDirection.ASC) ? Sort.Direction.ASC: Sort.Direction.DESC;
        return jobInfoRepository.findByKeyword(keyword.toLowerCase(), PageRequest.of(page, size, direction, attribute));
    }

    @Override
    public Page<JobInfo> fetchJobInfos(Integer page, Integer size, SortDirection sortDirection, String attribute) {
        Sort.Direction direction = sortDirection.equals(SortDirection.ASC) ? Sort.Direction.ASC: Sort.Direction.DESC;
        return jobInfoRepository.findAll(PageRequest.of(page, size, direction, attribute));
    }

    private void checkDuplicateJobInfo(JobInfo jobInfo) {
        if(jobInfo != null && jobInfoRepository.existsByJobNameIgnoreCase(jobInfo.getJobName())) {
            throw new JobInfoConflictException(
                    String.format("A job with name '%s' already exist in database. Please provide another name and try again.",
                            jobInfo.getJobName()));
        }
        if(jobInfo != null && jobInfoRepository.existsByJobClassIgnoreCase(jobInfo.getJobClass())) {
            throw new JobInfoConflictException(
                    String.format("A job with class name '%s' already exist in database. Please provide another class name and try again.",
                            jobInfo.getJobName()));
        }

    }
}
