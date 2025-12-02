package com.aajumaharjan.demofeatures.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Slf4j
public class RabbitAsyncMessagingClient implements AsyncMessagingClient {
    private final RabbitTemplate rabbitTemplate;
    private final ConnectionFactory connectionFactory;

    public RabbitAsyncMessagingClient(RabbitTemplate rabbitTemplate, ConnectionFactory connectionFactory) {
        this.rabbitTemplate = rabbitTemplate;
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void publish(String destination, String payload) {
        rabbitTemplate.convertAndSend(destination, payload);
    }

    @Override
    public void registerListener(String destination, Consumer<String> handler) {
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
}
