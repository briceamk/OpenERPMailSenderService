package cm.xenonit.gelodia.openerpmailsender.schedule.bootstrap;

import cm.xenonit.gelodia.openerpmailsender.schedule.service.JobInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author bamk
 * @version 1.0
 * @since 17/02/2024
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobInfoStartUpHandler implements ApplicationRunner {

    private final JobInfoService jobInfoService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info(">>>> Schedule all new job info at app startup - starting...");
        jobInfoService.startAllSchedulers();
        log.info(">>>> Schedule all new job info at app startup - complete...");
    }
}
