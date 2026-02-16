package com.kafkawars.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkawars.api.SignedCommand;
import com.kafkawars.domain.GridPosition;
import com.kafkawars.domain.MoveCommand;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.UUID;

public class CommandSender {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendMoveCommand(String playerId, String unitId, String matchId, GridPosition target) throws Exception {
        // 1. Create the core command
        MoveCommand moveCommand = new MoveCommand(playerId, unitId, matchId, target, "MOVE");

        // 2. Sign the command (using placeholder logic)
        String payloadJson = objectMapper.writeValueAsString(moveCommand);
        long timestamp = Instant.now().getEpochSecond();
        String nonce = UUID.randomUUID().toString();
        String signature = sign(payloadJson, timestamp, nonce);

        SignedCommand signedCommand = new SignedCommand(objectMapper.readTree(payloadJson), signature, timestamp, nonce);

        // 3. Serialize the signed command and send it
        String signedCommandJson = objectMapper.writeValueAsString(signedCommand);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ClientConfig.COMMAND_ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(signedCommandJson))
                .build();

        System.out.println("Sending command: " + signedCommandJson);
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Received response: " + response.statusCode() + " " + response.body());
    }

    /**
     * Placeholder for the real cryptographic signing logic (e.g., HMAC or ECDSA).
     */
    private String sign(String payload, long timestamp, String nonce) {
        // In a real client, this would use the player's private key to create a proper signature.
        // For now, we just create a hash of the content as a placeholder.
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            String dataToSign = payload + timestamp + nonce;
            byte[] hash = digest.digest(dataToSign.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            return "signature-placeholder";
        }
    }
}
