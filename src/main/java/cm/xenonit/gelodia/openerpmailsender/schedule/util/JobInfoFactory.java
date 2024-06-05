package cm.xenonit.gelodia.openerpmailsender.schedule.util;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

/**
 * @author bamk
 * @version 1.0
 * @since 17/02/2024
 */
public class JobInfoFactory extends SpringBeanJobFactory implements ApplicationContextAware {
    private AutowireCapableBeanFactory beanFactory;
    @Override
    public void setApplicationContext(final ApplicationContext context) {
        this.beanFactory = context.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
        final Object job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);
        return job;
    }
}
