package com.kafkawars.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkawars.domain.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class GameStateWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(GameStateWebSocketHandler.class);

    private final Map<String, List<WebSocketSession>> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public GameStateWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode json = objectMapper.readTree(message.getPayload());
        if (json.has("matchId")) {
            String matchId = json.get("matchId").asText();
            sessions.computeIfAbsent(matchId, k -> new CopyOnWriteArrayList<>()).add(session);
            session.getAttributes().put("matchId", matchId);
            log.info("WebSocket session {} subscribed to match {}", session.getId(), matchId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String matchId = (String) session.getAttributes().get("matchId");
        if (matchId != null) {
            List<WebSocketSession> matchSessions = sessions.get(matchId);
            if (matchSessions != null) {
                matchSessions.remove(session);
            }
        }
    }

    public void broadcastState(String matchId, GameState state) {
        List<WebSocketSession> matchSessions = sessions.getOrDefault(matchId, List.of());
        if (matchSessions.isEmpty()) return;

        try {
            String json = objectMapper.writeValueAsString(state);
            TextMessage msg = new TextMessage(json);
            for (WebSocketSession s : matchSessions) {
                if (s.isOpen()) {
                    s.sendMessage(msg);
                }
            }
        } catch (Exception e) {
            log.error("Error broadcasting state for match {}: {}", matchId, e.getMessage());
        }
    }
}
