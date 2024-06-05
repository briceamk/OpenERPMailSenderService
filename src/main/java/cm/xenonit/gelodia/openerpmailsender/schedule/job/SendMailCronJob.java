package cm.xenonit.gelodia.openerpmailsender.schedule.job;

import cm.xenonit.gelodia.openerpmailsender.notification.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author bamk
 * @version 1.0
 * @since 17/02/2024
 */
@Slf4j
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class SendMailCronJob extends QuartzJobBean {

    private final MailService mailService;
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info(">>>>  Sending mail cron job - starting...");
        mailService.sendMails();
        log.info(">>>>  Sending mail cron job - complete...");
    }
}
