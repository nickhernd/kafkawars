package com.kafkawars.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkawars.data.GameStateRepository;
import com.kafkawars.domain.AttackCommand;
import com.kafkawars.domain.GameState;
import com.kafkawars.domain.GameStatus;
import com.kafkawars.domain.MoveCommand;
import com.kafkawars.domain.events.GameEnded;
import com.kafkawars.logic.GameEngine;
import com.kafkawars.logic.ProcessingResult;
import com.kafkawars.websocket.GameStateWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import com.kafkawars.config.KafkaTopicConfig;

@Service
public class GameLoopService {

    private static final Logger log = LoggerFactory.getLogger(GameLoopService.class);

    private final GameEngine gameEngine;
    private final GameStateRepository gameStateRepository;
    private final EventProducer eventProducer;
    private final GameStateWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    public GameLoopService(GameEngine gameEngine, GameStateRepository gameStateRepository,
                           EventProducer eventProducer, GameStateWebSocketHandler webSocketHandler,
                           ObjectMapper objectMapper) {
        this.gameEngine = gameEngine;
        this.gameStateRepository = gameStateRepository;
        this.eventProducer = eventProducer;
        this.webSocketHandler = webSocketHandler;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
        topics = KafkaTopicConfig.COMMANDS_TOPIC,
        groupId = "kafka-wars-engine",
        concurrency = "3"
    )
    public void consumeCommand(@Payload String payload, @Header(KafkaHeaders.RECEIVED_KEY) String matchId) {
        try {
            JsonNode json = objectMapper.readTree(payload);
            String actionType = json.has("actionType") ? json.get("actionType").asText() : "";

            if ("MOVE".equals(actionType)) {
                MoveCommand command = objectMapper.treeToValue(json, MoveCommand.class);
                if (!matchId.equals(command.matchId())) {
                    log.error("Key {} does not match matchId {}. Discarding.", matchId, command.matchId());
                    return;
                }
                handleMove(matchId, command);
            } else if ("ATTACK".equals(actionType)) {
                AttackCommand command = objectMapper.treeToValue(json, AttackCommand.class);
                if (!matchId.equals(command.matchId())) {
                    log.error("Key {} does not match matchId {}. Discarding.", matchId, command.matchId());
                    return;
                }
                handleAttack(matchId, command);
            } else {
                log.warn("Unknown actionType '{}' for match {}", actionType, matchId);
            }
        } catch (Exception e) {
            log.error("Error processing command for match {}: {}", matchId, e.getMessage());
        }
    }

    private void handleMove(String matchId, MoveCommand command) {
        GameState currentState = loadOrCreate(matchId);
        if (currentState.status() != GameStatus.ACTIVE) {
            log.warn("Ignoring MOVE for match {} with status {}", matchId, currentState.status());
            return;
        }

        ProcessingResult result = gameEngine.processMove(currentState, command);
        switch (result) {
            case ProcessingResult.Success success -> {
                GameState nextState = currentState.updateUnitPosition(
                    success.event().unitId(), success.event().newPosition());
                gameStateRepository.save(matchId, nextState);
<<<<<<< HEAD
<<<<<<< HEAD
                eventProducer.publish(success.event());
                webSocketHandler.broadcastState(matchId, nextState);
                log.info("Move applied for match {}", matchId);
            }
            case ProcessingResult.Failure failure -> {
                eventProducer.publish(failure.event());
                log.warn("Move rejected for match {}: {}", matchId, failure.event().reason());
=======
=======
>>>>>>> 151bd7b (bugs correction)
                try {
                    eventProducer.publish(success.event());
                } catch (Exception e) {
                    log.error("CRITICAL: State saved for match {} but event publish failed. Manual reconciliation may be needed. Event: {}", matchId, success.event(), e);
                }
                log.info("State updated for match {}. New state hash: {}", matchId, nextState.hashCode());
            }
            case ProcessingResult.Failure failure -> {
                try {
                    eventProducer.publish(failure.event());
                } catch (Exception e) {
                    log.error("Failed to publish rejection event for match {}: {}", matchId, failure.event(), e);
                }
                log.warn("Command rejected for match {}: {}", matchId, failure.event().reason());
>>>>>>> 151bd7b (bugs correction)
            }
            default -> log.warn("Unexpected result type for MOVE in match {}", matchId);
        }
    }

    private void handleAttack(String matchId, AttackCommand command) {
        GameState currentState = loadOrCreate(matchId);
        if (currentState.status() != GameStatus.ACTIVE) {
            log.warn("Ignoring ATTACK for match {} with status {}", matchId, currentState.status());
            return;
        }

        ProcessingResult result = gameEngine.processAttack(currentState, command);
        switch (result) {
            case ProcessingResult.AttackHit hit -> {
                GameState nextState = currentState.applyHit(hit.event().targetUnitId());
                gameStateRepository.save(matchId, nextState);
                eventProducer.publish(hit.event());
                webSocketHandler.broadcastState(matchId, nextState);

                if (nextState.status() == GameStatus.FINISHED) {
                    String winnerUnitId = nextState.units().entrySet().stream()
                        .filter(e -> e.getValue().playerId().equals(nextState.winnerId()))
                        .map(e -> e.getKey())
                        .findFirst().orElse(null);
                    eventProducer.publish(new GameEnded(matchId, nextState.winnerId(), winnerUnitId));
                    log.info("Game {} finished. Winner: {}", matchId, nextState.winnerId());
                }
            }
            case ProcessingResult.AttackMiss miss -> {
                eventProducer.publish(miss.event());
                log.info("Shot missed for match {} by unit {}", matchId, miss.event().attackerUnitId());
            }
            default -> log.warn("Unexpected result type for ATTACK in match {}", matchId);
        }
    }

    private GameState loadOrCreate(String matchId) {
        return gameStateRepository.findByMatchId(matchId)
            .orElseGet(() -> {
                log.info("Creating initial state for match '{}'", matchId);
                return gameStateRepository.createInitialState(matchId);
            });
    }
}
