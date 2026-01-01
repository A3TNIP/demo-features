package com.aajumaharjan.demofeatures.async;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(EnableRabbit.class)
@ConditionalOnProperty(prefix = "plugins.demo-features.async", name = "enabled", matchIfMissing = true)
@EnableRabbit
public class RabbitListenerConfiguration {
    // Enables @RabbitListener for host applications when async is enabled and AMQP classes are present
}
