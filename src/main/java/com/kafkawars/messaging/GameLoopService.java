package com.kafkawars.messaging;

import com.kafkawars.data.GameStateRepository;
import com.kafkawars.domain.GameState;
import com.kafkawars.domain.MoveCommand;
import com.kafkawars.logic.GameEngine;
import com.kafkawars.logic.ProcessingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;
import com.kafkawars.config.KafkaTopicConfig;


@Service
public class GameLoopService {

    private static final Logger log = LoggerFactory.getLogger(GameLoopService.class);

    private final GameEngine gameEngine;
    private final GameStateRepository gameStateRepository;
    private final EventProducer eventProducer;

    public GameLoopService(GameEngine gameEngine, GameStateRepository gameStateRepository, EventProducer eventProducer) {
        this.gameEngine = gameEngine;
        this.gameStateRepository = gameStateRepository;
        this.eventProducer = eventProducer;
    }

    @KafkaListener(
        topics = KafkaTopicConfig.COMMANDS_TOPIC,
        groupId = "kafka-wars-engine",
        // Assumes JSON deserialization is configured for MoveCommand
        concurrency = "3" // Corresponds to the number of partitions
    )
    public void consumeCommand(@Payload MoveCommand command, @Header(KafkaHeaders.RECEIVED_KEY) String matchId) {
        log.info("Received command for match {}: {}", matchId, command);

        // defensive check to ensure command and message key match
        if (!matchId.equals(command.matchId())) {
            log.error("CRITICAL: Message key {} does not match command's matchId {}. Discarding.", matchId, command.matchId());
            return;
        }

        // 1. Load current state
        GameState currentState = gameStateRepository.findByMatchId(matchId);

        // 2. Process command with the game engine
        ProcessingResult result = gameEngine.processMove(currentState, command);

        // 3. Handle result
        switch (result) {
            case ProcessingResult.Success success -> {
                // On success, update the state and publish the event
                GameState nextState = currentState.updateUnitPosition(
                    success.event().unitId(),
                    success.event().newPosition()
                );
                gameStateRepository.save(matchId, nextState);
                eventProducer.publish(success.event());
                log.info("State updated for match {}. New state hash: {}", matchId, nextState.hashCode());
            }
            case ProcessingResult.Failure failure -> {
                // On failure, just publish the rejection event. State is not changed.
                eventProducer.publish(failure.event());
                log.warn("Command rejected for match {}: {}", matchId, failure.event().reason());
            }
        }
    }
}
