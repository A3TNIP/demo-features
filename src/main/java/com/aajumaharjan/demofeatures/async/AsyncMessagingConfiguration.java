package com.aajumaharjan.demofeatures.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@EnableConfigurationProperties(AsyncProperties.class)
public class AsyncMessagingConfiguration {
    private static final Logger log = LoggerFactory.getLogger(AsyncMessagingConfiguration.class);

    @Bean
    public AsyncMessagingClient asyncMessagingClient(AsyncProperties properties,
                                                     ObjectProvider<RabbitTemplate> rabbitTemplateProvider,
                                                     ObjectProvider<ConnectionFactory> connectionFactoryProvider,
                                                     ObjectProvider<KafkaTemplate<String, String>> kafkaTemplateProvider,
                                                     ObjectProvider<ConsumerFactory<String, String>> consumerFactoryProvider) {
        if (properties.getClientClass() != null && !properties.getClientClass().isBlank()) {
            return instantiateCustomClient(properties.getClientClass(), properties.getEndpoint());
        }

        return switch (properties.getProvider()) {
            case LOGGING -> new LoggingAsyncMessagingClient("logging", properties.getEndpoint());
            case NOOP -> new NoopAsyncMessagingClient();
            case RABBITMQ -> createRabbit(rabbitTemplateProvider, connectionFactoryProvider, properties.getEndpoint());
            case KAFKA -> createKafka(kafkaTemplateProvider, consumerFactoryProvider);
        };
    }

    private AsyncMessagingClient instantiateCustomClient(String className, String endpoint) {
        try {
            Class<?> raw = Class.forName(className);
            if (!AsyncMessagingClient.class.isAssignableFrom(raw)) {
                throw new IllegalArgumentException("Class " + className + " does not implement AsyncMessagingClient");
            }
            @SuppressWarnings("unchecked")
            Class<? extends AsyncMessagingClient> clientClass = (Class<? extends AsyncMessagingClient>) raw;
            try {
                return clientClass.getConstructor(String.class).newInstance(endpoint);
            } catch (NoSuchMethodException ignored) {
                return clientClass.getConstructor().newInstance();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to instantiate async client " + className + ": " + e.getMessage(), e);
        }
    }

    private AsyncMessagingClient createRabbit(ObjectProvider<RabbitTemplate> rabbitTemplateProvider,
                                              ObjectProvider<ConnectionFactory> connectionFactoryProvider,
                                              String endpoint) {
        RabbitTemplate template = rabbitTemplateProvider.getIfAvailable();
        ConnectionFactory cf = connectionFactoryProvider.getIfAvailable();
        if (template == null || cf == null) {
            log.warn("RabbitMQ requested but RabbitTemplate/ConnectionFactory not available; falling back to logging client");
            return new LoggingAsyncMessagingClient("rabbitmq-missing", endpoint);
        }
        return new RabbitAsyncMessagingClient(template, cf);
    }

    private AsyncMessagingClient createKafka(ObjectProvider<KafkaTemplate<String, String>> kafkaTemplateProvider,
                                             ObjectProvider<ConsumerFactory<String, String>> consumerFactoryProvider) {
        KafkaTemplate<String, String> template = kafkaTemplateProvider.getIfAvailable();
        ConsumerFactory<String, String> cf = consumerFactoryProvider.getIfAvailable();
        if (template == null || cf == null) {
            log.warn("Kafka requested but KafkaTemplate/ConsumerFactory not available; falling back to logging client");
            return new LoggingAsyncMessagingClient("kafka-missing", "default");
        }
        return new KafkaAsyncMessagingClient(template, cf);
    }
}
