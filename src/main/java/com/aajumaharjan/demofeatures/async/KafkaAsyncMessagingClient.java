package com.aajumaharjan.demofeatures.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;

import java.util.function.Consumer;

public class KafkaAsyncMessagingClient extends AbstractAsyncMessagingClient implements AsyncMessagingClient {
    private static final Logger log = LoggerFactory.getLogger(KafkaAsyncMessagingClient.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ConsumerFactory<String, String> consumerFactory;
    private final KafkaAdmin kafkaAdmin;

    public KafkaAsyncMessagingClient(KafkaTemplate<String, String> kafkaTemplate,
                                     ConsumerFactory<String, String> consumerFactory,
                                     KafkaAdmin kafkaAdmin) {
        this.kafkaTemplate = kafkaTemplate;
        this.consumerFactory = consumerFactory;
        this.kafkaAdmin = kafkaAdmin;
    }

    @Override
    public void publish(String destination, Object payload) {
        ensureTopicExists(destination);
        kafkaTemplate.send(destination, serialize(payload));
    }

    @Override
    public <T> void registerListener(String destination, Class<T> type, Consumer<T> handler) {
        ensureTopicExists(destination);
        ContainerProperties props = new ContainerProperties(destination);
        props.setMessageListener((MessageListener<String, String>) record -> handler.accept(deserialize(record.value(), type)));
        ConcurrentMessageListenerContainer<String, String> container =
                new ConcurrentMessageListenerContainer<>(consumerFactory, props);
        container.start();
        log.info("Kafka listener started for topic {}", destination);
    }

    private void ensureTopicExists(String topic) {
        if (kafkaAdmin == null) {
            log.debug("KafkaAdmin not available; skipping topic declaration for {}", topic);
            return;
        }
        try {
            kafkaAdmin.createOrModifyTopics(org.springframework.kafka.config.TopicBuilder.name(topic).partitions(1).replicas(1).build());
        } catch (Exception e) {
            log.warn("Topic creation check failed for {}: {}", topic, e.getMessage());
        }
    }
}
