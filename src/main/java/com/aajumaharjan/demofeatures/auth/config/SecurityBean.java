package com.aajumaharjan.demofeatures.auth.config;

import com.aajumaharjan.demofeatures.auth.AuthProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

@Configuration
public class SecurityBean {

    private final AuthProperties authProperties;

    public SecurityBean(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    @Bean
    public PasswordEncoder encoder(){
        if (authProperties.getPassword().getEncoder() == AuthProperties.Password.Encoder.PBKDF2) {
            // Use Spring's vetted defaults for PBKDF2 to avoid platform-specific issues
            return Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        }
        return new BCryptPasswordEncoder();
    }

}
