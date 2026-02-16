package com.kafkawars.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String COMMANDS_TOPIC = "game.commands";
    public static final String EVENTS_TOPIC = "game.events";

    @Bean
    public NewTopic gameCommandsTopic() {
        return TopicBuilder.name(COMMANDS_TOPIC)
                .partitions(3) // As per the original design for match-based partitioning
                .replicas(3)   // Matching the broker count
                .build();
    }

    @Bean
    public NewTopic gameEventsTopic() {
        return TopicBuilder.name(EVENTS_TOPIC)
                .partitions(1)
                .replicas(3)   // High availability for events
                .compact()     // Use log compaction for game state events
                .build();
    }
}
