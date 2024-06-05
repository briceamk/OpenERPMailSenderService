package cm.xenonit.gelodia.openerpmailsender.schedule.job;

import cm.xenonit.gelodia.openerpmailsender.openerp.service.implementation.RemoteOpenERPService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlrpc.XmlRpcException;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.net.MalformedURLException;

/**
 * @author bamk
 * @version 1.0
 * @since 17/02/2024
 */
@Slf4j
@RequiredArgsConstructor
@DisallowConcurrentExecution
public class DeleteRemoteMailInQueueStateJob extends QuartzJobBean {

    private final RemoteOpenERPService remoteOpenERPService;
    @Override
    protected void executeInternal(JobExecutionContext context) {
        log.info(">>>>  Delete Remote Mail In Queue - starting...");
        try {
            remoteOpenERPService.deleteRemoteEmailInQueue();
        } catch (XmlRpcException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
        log.info(">>>>  Delete Remote Mail In Queue - complete...");
    }
}
