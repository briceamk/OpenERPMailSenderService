package cm.xenonit.gelodia.openerpmailsender.security.config;

import cm.xenonit.gelodia.openerpmailsender.security.security.CustomAccessDeniedHandler;
import cm.xenonit.gelodia.openerpmailsender.security.security.CustomAuthenticationEntryPoint;
import cm.xenonit.gelodia.openerpmailsender.security.security.CustomAuthorizationFilter;
import cm.xenonit.gelodia.openerpmailsender.security.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import static cm.xenonit.gelodia.openerpmailsender.security.constant.SecurityConstant.PUBLIC_URLS;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAuthorizationFilter customAuthorizationFilter;
    private final AuthenticationEventPublisher authenticationEventPublisher;
    private final CorsFilter corsFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(request -> request.requestMatchers(PUBLIC_URLS).permitAll())
                .authorizeHttpRequests(request -> request.anyRequest().authenticated())
                .exceptionHandling( exception ->
                        exception
                                .accessDeniedHandler(customAccessDeniedHandler)
                                .authenticationEntryPoint(customAuthenticationEntryPoint))
                .addFilterBefore(customAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
        ProviderManager providerManager = new ProviderManager(authenticationProvider);
        providerManager.setAuthenticationEventPublisher(authenticationEventPublisher);
        return providerManager;
    }


}
