package com.kafkawars.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkawars.domain.MoveCommand;
import com.kafkawars.messaging.CommandProducer;
import com.kafkawars.security.SecurityValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class CommandController {

    private static final Logger log = LoggerFactory.getLogger(CommandController.class);

    private final SecurityValidator securityValidator;
    private final CommandProducer commandProducer;
    private final ObjectMapper objectMapper;

    public CommandController(SecurityValidator securityValidator, CommandProducer commandProducer, ObjectMapper objectMapper) {
        this.securityValidator = securityValidator;
        this.commandProducer = commandProducer;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/command")
    public ResponseEntity<Void> submitCommand(@RequestBody SignedCommand signedCommand) {
        // 1. Perform stateless validation (signature, timestamp, nonce)
        if (!securityValidator.isValid(signedCommand)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // 2. Check for actionType before deserializing
            JsonNode payload = signedCommand.payload();
            if (payload == null || !payload.has("actionType")) {
                log.warn("Received command with no actionType.");
                return ResponseEntity.badRequest().build();
            }

            // 3. Deserialize the inner payload to a specific command type.
            if ("MOVE".equals(payload.get("actionType").asText())) {
                MoveCommand moveCommand = objectMapper.treeToValue(payload, MoveCommand.class);
                
                if (moveCommand.matchId() == null || moveCommand.matchId().isBlank()) {
                    log.warn("MoveCommand is missing a matchId.");
                    return ResponseEntity.badRequest().build();
                }

                // 4. Publish the validated, deserialized command to Kafka, using matchId as the key.
                commandProducer.publish(moveCommand, moveCommand.matchId());

                return ResponseEntity.accepted().build();
            } else {
                 log.warn("Received command with unknown actionType: {}", payload.get("actionType").asText());
                 return ResponseEntity.badRequest().build();
            }

        } catch (Exception e) {
            log.error("Error processing command payload: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
