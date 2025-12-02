package com.aajumaharjan.demofeatures.async;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;

import java.util.function.Consumer;

public class KafkaAsyncMessagingClient implements AsyncMessagingClient {
    private static final Logger log = LoggerFactory.getLogger(KafkaAsyncMessagingClient.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConsumerFactory<String, String> consumerFactory;

    public KafkaAsyncMessagingClient(KafkaTemplate<String, String> kafkaTemplate,
                                     ConsumerFactory<String, String> consumerFactory) {
        this.kafkaTemplate = kafkaTemplate;
        this.consumerFactory = consumerFactory;
    }

    @Override
    public void publish(String destination, Object payload) {
        kafkaTemplate.send(destination, serialize(payload));
    }

    @Override
    public <T> void registerListener(String destination, Class<T> type, Consumer<T> handler) {
        ContainerProperties props = new ContainerProperties(destination);
        props.setMessageListener((MessageListener<String, String>) record -> handler.accept(deserialize(record.value(), type)));
        ConcurrentMessageListenerContainer<String, String> container =
                new ConcurrentMessageListenerContainer<>(consumerFactory, props);
        container.start();
        log.info("Kafka listener started for topic {}", destination);
    }

    public String serialize(Object payload) {
        try {
            return mapper.writeValueAsString(payload);
        } catch (Exception e) {
            log.warn("Failed to serialize payload {}, sending as string: {}", payload, e.getMessage());
            return String.valueOf(payload);
        }
    }

    public <T> T deserialize(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            log.warn("Failed to deserialize message to {}: {}", type.getSimpleName(), e.getMessage());
            return type.cast(json);
        }
    }
}
