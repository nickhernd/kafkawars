package com.kafkawars.messaging;

import com.kafkawars.config.KafkaTopicConfig;
import com.kafkawars.domain.events.GameEvent;
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

    public void publish(GameEvent event) {
        log.info("Publishing event: {}", event);
        kafkaTemplate.send(KafkaTopicConfig.EVENTS_TOPIC, event);
    }
}
