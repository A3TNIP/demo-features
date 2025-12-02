package com.aajumaharjan.demofeatures.auth.config;

import com.aajumaharjan.demofeatures.auth.AuthProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    private final UnauthorizedEntryPoint unauthorizedEntryPoint;
    private final AuthProperties authProperties;
    private final TokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    public WebSecurityConfig(UnauthorizedEntryPoint unauthorizedEntryPoint,
                             AuthProperties authProperties,
                             TokenProvider tokenProvider,
                             UserDetailsService userDetailsService) {
        this.unauthorizedEntryPoint = unauthorizedEntryPoint;
        this.authProperties = authProperties;
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        SessionCreationPolicy sessionPolicy = authProperties.getType() == com.aajumaharjan.demofeatures.auth.AuthProperties.Type.STATEFUL
                ? SessionCreationPolicy.IF_REQUIRED
                : SessionCreationPolicy.STATELESS;

        String[] openRoutes = authProperties.getPublicRoutes().toArray(new String[0]);

        var httpConfigured = http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .exceptionHandling((exception) -> exception.authenticationEntryPoint(unauthorizedEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(sessionPolicy))
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(openRoutes).permitAll()
                        .anyRequest().authenticated());

        // Only add JWT bearer filter in stateless mode
        if (sessionPolicy == SessionCreationPolicy.STATELESS) {
            httpConfigured.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
        } else {
            // Stateful mode: use standard session-based auth mechanisms
            httpConfigured
                    .formLogin(Customizer.withDefaults())
                    .httpBasic(Customizer.withDefaults())
                    .logout(Customizer.withDefaults());
        }

        return httpConfigured.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter authenticationTokenFilterBean() {
        return new JwtAuthenticationFilter(authProperties, userDetailsService, tokenProvider, unauthorizedEntryPoint);
    }
}
