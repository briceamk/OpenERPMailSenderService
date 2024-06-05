package cm.xenonit.gelodia.openerpmailsender.security.config;

import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static cm.xenonit.gelodia.openerpmailsender.security.constant.SecurityConstant.STRENGTH;
import static java.util.List.of;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
@Configuration
public class GlobalSecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(STRENGTH);
    }

    @Bean
    public AuthenticationEventPublisher authenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        return new DefaultAuthenticationEventPublisher(applicationEventPublisher);
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(of("http://localhost:4200", "http://localhost:8090", "http://81.0.220.138:8090"));
        corsConfiguration.setAllowedHeaders(of("Origin", "Content-Type", "Access-Control-Allow-Origin", "Accept",
                "Jwt-Token", "Authorization", "Accept", "X-Requested-With", "Access-Control-Request-Method",
                "Access-Control-Request-Headers"));
        corsConfiguration.setExposedHeaders(of("Origin", "Content-Type", "Accept", "Jwt-Token", "Authorization",
                "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "File-Name"));
        corsConfiguration.setAllowedMethods(of("GET", "POST", "OPTIONS", "PUT", "DELETE", "PATCH"));
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    @Bean
    public JsonNullableModule jsonNullableModule() {
        return new JsonNullableModule();
    }
}
