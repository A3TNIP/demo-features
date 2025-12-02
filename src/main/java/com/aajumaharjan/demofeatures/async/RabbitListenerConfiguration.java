package com.aajumaharjan.demofeatures.async;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(EnableRabbit.class)
@ConditionalOnProperty(prefix = "plugins.demo-features.async", name = "provider", havingValue = "RABBITMQ")
@EnableRabbit
public class RabbitListenerConfiguration {
    // Enables @RabbitListener for host applications when provider=RABBITMQ and amqp classes are present
}
