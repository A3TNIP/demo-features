package com.aajumaharjan.demofeatures.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class RabbitAsyncMessagingClient implements AsyncMessagingClient {
    private static final Logger log = LoggerFactory.getLogger(RabbitAsyncMessagingClient.class);
    private final RabbitTemplate rabbitTemplate;
    private final ConnectionFactory connectionFactory;
    private final RabbitAdmin rabbitAdmin;

    public RabbitAsyncMessagingClient(RabbitTemplate rabbitTemplate, ConnectionFactory connectionFactory, RabbitAdmin rabbitAdmin) {
        this.rabbitTemplate = rabbitTemplate;
        this.connectionFactory = connectionFactory;
        this.rabbitAdmin = rabbitAdmin;
    }

    @Override
    public void publish(String destination, String payload) {
        rabbitTemplate.convertAndSend(destination, payload);
    }

    @Override
    public void registerListener(String destination, Consumer<String> handler) {
        ensureQueueExists(destination);
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames(destination);
        container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
            handler.accept(convert(message));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        });
        container.start();
        log.info("RabbitMQ listener started for queue {}", destination);
    }

    private String convert(Message message) {
        return new String(message.getBody(), StandardCharsets.UTF_8);
    }

    private void ensureQueueExists(String destination) {
        if (rabbitAdmin == null) {
            log.warn("RabbitAdmin not available; skipping queue declaration for {}", destination);
            return;
        }
        rabbitAdmin.declareQueue(new Queue(destination, true));
    }
}
