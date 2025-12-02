package com.aajumaharjan.demofeatures.async;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import java.util.function.Consumer;

public class RabbitAsyncMessagingClient implements AsyncMessagingClient {
    private static final Logger log = LoggerFactory.getLogger(RabbitAsyncMessagingClient.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private final RabbitTemplate rabbitTemplate;
    private final ConnectionFactory connectionFactory;
    private final RabbitAdmin rabbitAdmin;
    private final MessageConverter messageConverter = new Jackson2JsonMessageConverter(mapper);

    public RabbitAsyncMessagingClient(RabbitTemplate rabbitTemplate, ConnectionFactory connectionFactory, RabbitAdmin rabbitAdmin) {
        this.rabbitTemplate = rabbitTemplate;
        this.connectionFactory = connectionFactory;
        this.rabbitAdmin = rabbitAdmin;
        this.rabbitTemplate.setMessageConverter(messageConverter);
    }

    @Override
    public void publish(String destination, Object payload) {
        ensureQueueExists(destination);
        rabbitTemplate.convertAndSend(destination, payload);
    }

    @Override
    public <T> void registerListener(String destination, Class<T> type, Consumer<T> handler) {
        ensureQueueExists(destination);
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames(destination);
        container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
            handler.accept(convert(message, type));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        });
        container.start();
        log.info("RabbitMQ listener started for queue {}", destination);
    }

    private <T> T convert(Message message, Class<T> type) {
        try {
            Object body = messageConverter.fromMessage(message);
            return type.cast(mapper.convertValue(body, type));
        } catch (Exception e) {
            log.warn("Failed to convert message to {}: {}", type.getSimpleName(), e.getMessage());
            return type.cast(new String(message.getBody()));
        }
    }

    private void ensureQueueExists(String destination) {
        if (rabbitAdmin == null) {
            log.warn("RabbitAdmin not available; skipping queue declaration for {}", destination);
            return;
        }
        rabbitAdmin.declareQueue(new Queue(destination, true));
    }
}
