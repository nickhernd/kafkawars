package com.kafkawars.messaging;

import com.kafkawars.config.KafkaTopicConfig;
import com.kafkawars.domain.PlayerAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Producer service specifically for publishing validated commands from the API layer.
 */
@Service
public class CommandProducer {

    private static final Logger log = LoggerFactory.getLogger(CommandProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CommandProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a command to the game.commands topic.
     * @param command The command payload (e.g., MoveCommand).
     * @param matchId The ID of the match, used as the partition key to ensure order.
     */
    public void publish(PlayerAction command, String matchId) {
        log.info("Publishing command for match {}: {}", matchId, command);
        kafkaTemplate.send(KafkaTopicConfig.COMMANDS_TOPIC, matchId, command);
    }
}
