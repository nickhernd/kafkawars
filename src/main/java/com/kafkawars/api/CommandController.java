package com.kafkawars.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkawars.domain.AttackCommand;
import com.kafkawars.domain.MoveCommand;
import com.kafkawars.messaging.CommandProducer;
import com.kafkawars.security.SecurityValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.kafkawars.data.GameStateRepository;
import com.kafkawars.domain.GameState;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class CommandController {

    private static final Logger log = LoggerFactory.getLogger(CommandController.class);

    private final SecurityValidator securityValidator;
    private final CommandProducer commandProducer;
    private final ObjectMapper objectMapper;
    private final GameStateRepository gameStateRepository;

    public CommandController(SecurityValidator securityValidator, CommandProducer commandProducer,
                             ObjectMapper objectMapper, GameStateRepository gameStateRepository) {
        this.securityValidator = securityValidator;
        this.commandProducer = commandProducer;
        this.objectMapper = objectMapper;
        this.gameStateRepository = gameStateRepository;
    }

    @GetMapping("/state/{matchId}")
    public ResponseEntity<GameState> getGameState(@PathVariable String matchId) {
        return gameStateRepository.findByMatchId(matchId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
<<<<<<< HEAD
    }

    @GetMapping("/lobby")
    public ResponseEntity<LobbyState> getLobby() {
        return ResponseEntity.ok(gameStateRepository.getLobbyState());
    }

    @PostMapping("/lobby/join")
    public ResponseEntity<LobbyState> joinLobby(@RequestBody Map<String, String> request) {
        String playerId = request.get("playerId");
        if (playerId == null || playerId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        LobbyState lobbyState = gameStateRepository.joinLobby(playerId);
        return ResponseEntity.ok(lobbyState);
=======
>>>>>>> 151bd7b (bugs correction)
    }

    @PostMapping("/command")
    public ResponseEntity<Void> submitCommand(@RequestBody SignedCommand signedCommand) {
        if (!securityValidator.isValid(signedCommand)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            JsonNode payload = signedCommand.payload();
            if (payload == null || !payload.has("actionType")) {
                log.warn("Received command with no actionType.");
                return ResponseEntity.badRequest().build();
            }

            String actionType = payload.get("actionType").asText();

            if ("MOVE".equals(actionType)) {
                MoveCommand moveCommand = objectMapper.treeToValue(payload, MoveCommand.class);
                if (moveCommand.matchId() == null || moveCommand.matchId().isBlank()) {
                    log.warn("MoveCommand is missing a matchId.");
                    return ResponseEntity.badRequest().build();
                }
<<<<<<< HEAD
=======

>>>>>>> 151bd7b (bugs correction)
                commandProducer.publish(moveCommand, moveCommand.matchId());
                return ResponseEntity.accepted().build();

            } else if ("ATTACK".equals(actionType)) {
                AttackCommand attackCommand = objectMapper.treeToValue(payload, AttackCommand.class);
                if (attackCommand.matchId() == null || attackCommand.matchId().isBlank()) {
                    log.warn("AttackCommand is missing a matchId.");
                    return ResponseEntity.badRequest().build();
                }
                commandProducer.publish(attackCommand, attackCommand.matchId());
                return ResponseEntity.accepted().build();

            } else {
                log.warn("Received command with unknown actionType: {}", actionType);
                return ResponseEntity.badRequest().build();
            }

        } catch (Exception e) {
            log.error("Error processing command payload: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
