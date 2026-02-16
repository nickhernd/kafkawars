package com.kafkawars.security;

import com.kafkawars.api.SignedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Service responsible for stateless validation of incoming commands.
 * This includes cryptographic signature verification and replay attack prevention.
 */
@Service
public class SecurityValidator {

    private static final Logger log = LoggerFactory.getLogger(SecurityValidator.class);
    private static final long TIMESTAMP_WINDOW_SECONDS = 10; // Commands must be received within 10 seconds

    /**
     * Validates a signed command.
     *
     * @param command The command to validate.
     * @return true if the command is valid, false otherwise.
     */
    public boolean isValid(SignedCommand command) {
        // 1. Prevent replay attacks by checking the timestamp.
        if (!isTimestampValid(command.timestamp())) {
            log.warn("Invalid command: Timestamp {} is outside the acceptable window.", command.timestamp());
            return false;
        }

        // 2. Prevent replay attacks by checking the nonce.
        // A real implementation would cache recently used nonces in a store like Redis with a TTL.
        // For this skeleton, we assume the nonce is always new.
        if (isNonceReplayed(command.nonce())) {
             log.warn("Invalid command: Replayed nonce '{}'.", command.nonce());
             return false;
        }


        // 3. Verify the cryptographic signature.
        // This is a placeholder for the actual crypto logic (e.g., ECDSA or HMAC-SHA256).
        // A real implementation would:
        // a. Fetch the player's public key based on an ID in the payload.
        // b. Reconstruct the signed message string (e.g., payload.toString() + timestamp + nonce).
        // c. Use a crypto library to verify the signature against the message and public key.
        boolean signatureVerified = verifySignature(command.signature(), command.payload(), command.timestamp(), command.nonce());
        if (!signatureVerified) {
            log.warn("Invalid command: Signature '{}' could not be verified.", command.signature());
            return false;
        }

        log.info("Command signature and metadata are valid for nonce: {}", command.nonce());
        return true;
    }

    private boolean isTimestampValid(long commandTimestamp) {
        long now = Instant.now().getEpochSecond();
        return (now - commandTimestamp) < TIMESTAMP_WINDOW_SECONDS;
    }

    private boolean isNonceReplayed(String nonce) {
        // Placeholder: In a real system, this would check a distributed cache (e.g., Redis).
        return false;
    }

    private boolean verifySignature(String signature, Object payload, long timestamp, String nonce) {
        // Placeholder for actual cryptographic verification. For now, we trust all commands.
        // A non-empty signature is required, at least.
        return signature != null && !signature.isBlank();
    }
}
