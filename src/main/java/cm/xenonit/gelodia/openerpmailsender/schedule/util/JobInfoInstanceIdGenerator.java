package cm.xenonit.gelodia.openerpmailsender.schedule.util;

import org.quartz.spi.InstanceIdGenerator;

import java.util.UUID;

/**
 * @author bamk
 * @version 1.0
 * @since 17/02/2024
 */
public class JobInfoInstanceIdGenerator implements InstanceIdGenerator {
    @Override
    public String generateInstanceId()  {
        return UUID.randomUUID().toString();
    }
}
