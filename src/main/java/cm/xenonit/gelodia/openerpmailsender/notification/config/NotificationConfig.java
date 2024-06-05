package cm.xenonit.gelodia.openerpmailsender.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;

/**
 * @author bamk
 * @version 1.0
 * @since 11/02/2024
 */
@Configuration
public class NotificationConfig {

    public static final String EMAIL_TEMPLATE_FILE_SUFFIX = ".html";
    public static final String EMAIL_TEMPLATE_FOLDER = "/templates/";

    @Bean
    public SpringTemplateEngine springTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        return templateEngine;
    }
    @Bean
    public ClassLoaderTemplateResolver htmlTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix(EMAIL_TEMPLATE_FOLDER);
        templateResolver.setSuffix(EMAIL_TEMPLATE_FILE_SUFFIX);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        return templateResolver;
    }


}
