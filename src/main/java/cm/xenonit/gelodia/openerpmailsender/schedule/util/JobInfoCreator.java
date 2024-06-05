package cm.xenonit.gelodia.openerpmailsender.schedule.util;

import cm.xenonit.gelodia.openerpmailsender.schedule.exception.JobInfoTechnicalException;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

/**
 * @author bamk
 * @version 1.0
 * @since 17/02/2024
 */
@Component
public class JobInfoCreator {

    public JobDetail createJob(
            Class<? extends QuartzJobBean> jobClass,
            boolean isDurable,
            ApplicationContext context,
            String jobName,
            String jobGroup
    ) {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(jobName + jobGroup, jobClass.getName());

        factoryBean.setJobClass(jobClass);
        factoryBean.setDurability(isDurable);
        factoryBean.setApplicationContext(context);
        factoryBean.setName(jobName);
        factoryBean.setGroup(jobGroup);
        factoryBean.setJobDataMap(jobDataMap);

        factoryBean.afterPropertiesSet();

        return factoryBean.getObject();
    }

    public CronTrigger createCronTrigger(String triggerName, Date startTime, String cronExpression, int misFireInstruction) {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();

        factoryBean.setName(triggerName);
        factoryBean.setStartTime(startTime);
        factoryBean.setCronExpression(cronExpression);
        factoryBean.setMisfireInstruction(misFireInstruction);
        try {
            factoryBean.afterPropertiesSet();
            return factoryBean.getObject();
        } catch (ParseException exception) {
            throw new JobInfoTechnicalException(exception.getMessage());
        }
    }

    public SimpleTrigger createIntervalTrigger(String triggerName, Date startTime, Long interval, int msiFireInstruction) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();

        factoryBean.setName(triggerName);
        factoryBean.setStartTime(startTime);
        factoryBean.setRepeatInterval(interval);
        factoryBean.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        factoryBean.setMisfireInstruction(msiFireInstruction);

        factoryBean.afterPropertiesSet();

        return factoryBean.getObject();
    }
}
