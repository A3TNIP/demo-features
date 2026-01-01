package com.aajumaharjan.demofeatures.async;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AsyncProperties.class)
public class AsyncMessagingConfiguration {
    @Bean
    @ConditionalOnProperty(prefix = "plugins.demo-features.async", name = "enabled", matchIfMissing = true)
    public AsyncMessagingClient asyncMessagingClient(ObjectProvider<RabbitTemplate> rabbitTemplateProvider,
                                                     ObjectProvider<ConnectionFactory> connectionFactoryProvider,
                                                     ObjectProvider<RabbitAdmin> rabbitAdminProvider) {
        RabbitTemplate template = rabbitTemplateProvider.getIfAvailable();
        ConnectionFactory cf = connectionFactoryProvider.getIfAvailable();
        RabbitAdmin admin = rabbitAdminProvider.getIfAvailable();
        if (template == null || cf == null) {
            throw new IllegalStateException("RabbitMQ requested but RabbitTemplate/ConnectionFactory not available");
        }
        if (admin == null && template != null) {
            admin = new RabbitAdmin(template);
        }
        return new RabbitAsyncMessagingClient(template, cf, admin);
    }
}
