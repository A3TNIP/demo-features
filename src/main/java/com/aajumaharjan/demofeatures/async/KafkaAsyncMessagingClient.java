package com.aajumaharjan.demofeatures.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;

import java.util.function.Consumer;

@Slf4j
public class KafkaAsyncMessagingClient implements AsyncMessagingClient {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConsumerFactory<String, String> consumerFactory;

    public KafkaAsyncMessagingClient(KafkaTemplate<String, String> kafkaTemplate,
                                     ConsumerFactory<String, String> consumerFactory) {
        this.kafkaTemplate = kafkaTemplate;
        this.consumerFactory = consumerFactory;
    }

    @Override
    public void publish(String destination, String payload) {
        kafkaTemplate.send(destination, payload);
    }

    @Override
    public void registerListener(String destination, Consumer<String> handler) {
        ContainerProperties props = new ContainerProperties(destination);
        props.setMessageListener((MessageListener<String, String>) record -> handler.accept(record.value()));
        ConcurrentMessageListenerContainer<String, String> container =
                new ConcurrentMessageListenerContainer<>(consumerFactory, props);
        container.start();
        log.info("Kafka listener started for topic {}", destination);
    }
}
