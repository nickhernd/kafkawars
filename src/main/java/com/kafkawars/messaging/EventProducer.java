package com.kafkawars.messaging;

import com.kafkawars.config.KafkaTopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {

    private static final Logger log = LoggerFactory.getLogger(EventProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(Object event) {
        log.info("Publishing event: {}", event);
        // We will need a proper serialization strategy, but for now, this shows the intent.
        // The key for the event should likely be the match or unit ID for partitioning.
        kafkaTemplate.send(KafkaTopicConfig.EVENTS_TOPIC, event);
    }
}
