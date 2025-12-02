package com.aajumaharjan.demofeatures.async;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@ConditionalOnClass(EnableKafka.class)
@ConditionalOnProperty(prefix = "plugins.demo-features.async", name = "provider", havingValue = "KAFKA")
@EnableKafka
public class KafkaListenerConfiguration {
    // Enables @KafkaListener for host applications when provider=KAFKA and kafka is on classpath
}
