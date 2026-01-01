package com.aajumaharjan.demofeatures.auth;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.aajumaharjan.demofeatures.auth")
@EntityScan(basePackages = "com.aajumaharjan.demofeatures.auth.model")
@EnableConfigurationProperties(AuthProperties.class)
public class AuthFeatureConfiguration {
}
