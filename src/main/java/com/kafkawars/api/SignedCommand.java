package com.kafkawars.api;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * A wrapper for player commands that includes security metadata.
 * This is the object that the Ingress Gateway will receive.
 *
 * @param payload The actual game command (e.g., MoveCommand) as a generic JSON object.
 *                This allows the signature to be verified against the raw JSON payload.
 * @param signature The cryptographic signature (e.g., ECDSA or HMAC) of the payload + timestamp + nonce.
 * @param timestamp The UTC timestamp when the command was created. Used to prevent replay attacks.
 * @param nonce A unique, random string for this specific command to prevent replay attacks.
 */
public record SignedCommand(
    JsonNode payload,
    String signature,
    long timestamp,
    String nonce
) {
}
