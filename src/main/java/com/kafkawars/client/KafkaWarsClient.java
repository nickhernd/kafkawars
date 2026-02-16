package com.kafkawars.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.kafkawars.domain.GameState;
import com.kafkawars.domain.GridPosition;
import com.kafkawars.domain.UnitState;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReference;

public class KafkaWarsClient {

    private static String PLAYER_ID = "player-1"; // Default
    private static final String MATCH_ID = "match-1";

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final AtomicReference<GameState> gameState = new AtomicReference<>();
    private static final AtomicReference<String> status = new AtomicReference<>("Connecting...");
    private static final CommandSender commandSender = new CommandSender();

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            PLAYER_ID = args[0];
            status.set("Registered as " + PLAYER_ID);
        }

        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        Terminal terminal = defaultTerminalFactory.createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();
        screen.setCursorPosition(null);

        HttpClient.newHttpClient().newWebSocketBuilder()
                .buildAsync(URI.create(ClientConfig.WEBSOCKET_URL), new GameWebSocketListener());

        try {
            int cursorX = 0;
            int cursorY = 0;
            String selectedUnitId = null;
            boolean running = true;

            while (running) {
                KeyStroke keyStroke = screen.pollInput();
                if (keyStroke != null) {
                    if (keyStroke.getKeyType() == KeyType.Character && keyStroke.getCharacter() == 'q') {
                        running = false;
                    }
                    if (keyStroke.getKeyType() == KeyType.ArrowUp) cursorY = Math.max(0, cursorY - 1);
                    if (keyStroke.getKeyType() == KeyType.ArrowDown) cursorY = Math.min(9, cursorY + 1);
                    if (keyStroke.getKeyType() == KeyType.ArrowLeft) cursorX = Math.max(0, cursorX - 1);
                    if (keyStroke.getKeyType() == KeyType.ArrowRight) cursorX = Math.min(9, cursorX + 1);

                    if (keyStroke.getKeyType() == KeyType.Enter) {
                        final GridPosition cursorPosition = new GridPosition(cursorX, cursorY);
                        GameState currentState = gameState.get();
                        
                        if (selectedUnitId == null) {
                            // Try to select a unit
                            Optional<String> unitAtCursor = getUnitIdAtPosition(currentState, cursorPosition);
                            if (unitAtCursor.isPresent()) {
                                // Check ownership
                                UnitState unitState = currentState.units().get(unitAtCursor.get());
                                if (unitState != null && unitState.playerId().equals(PLAYER_ID)) {
                                    selectedUnitId = unitAtCursor.get();
                                    status.set("Selected unit: " + selectedUnitId);
                                } else {
                                    status.set("Cannot select opponent's unit.");
                                }
                            }
                        } else {
                            // Send move command
                            try {
                                status.set("Sending move command for " + selectedUnitId + " to " + cursorPosition);
                                commandSender.sendMoveCommand(PLAYER_ID, selectedUnitId, MATCH_ID, cursorPosition);
                            } catch (Exception e) {
                                status.set("Error sending command: " + e.getMessage());
                            } finally {
                                selectedUnitId = null; // De-select after sending
                            }
                        }
                    }
                }

                screen.clear();
                final TextGraphics tg = screen.newTextGraphics();
                
                draw(tg, 10, 10, gameState.get(), cursorX, cursorY, selectedUnitId);

                tg.putString(0, 12, "Player: " + PLAYER_ID);
                tg.putString(0, 13, "Status: " + status.get());
                tg.putString(0, 14, "Cursor: (" + cursorX + "," + cursorY + ")");
                
                screen.refresh();
                Thread.sleep(1000 / 30);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (screen != null) {
                screen.close();
            }
            System.out.println("--- KafkaWars Client Shutting Down ---");
        }
    }

    private static Optional<String> getUnitIdAtPosition(GameState state, GridPosition position) {
        if (state == null) return Optional.empty();
        return state.units().entrySet().stream()
                .filter(entry -> entry.getValue().position().equals(position))
                .map(Map.Entry::getKey)
                .findFirst();
    }

    private static void draw(TextGraphics tg, int width, int height, GameState state, int cursorX, int cursorY, String selectedUnitId) {
        tg.setForegroundColor(TextColor.ANSI.WHITE);
        tg.setBackgroundColor(TextColor.ANSI.BLACK);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tg.putString(x * 2 + 1, y + 1, " .");
            }
        }

        if (state != null) {
            for (Map.Entry<String, UnitState> entry : state.units().entrySet()) {
                UnitState unit = entry.getValue();
                GridPosition pos = unit.position();
                
                if (Objects.equals(entry.getKey(), selectedUnitId)) {
                    tg.setBackgroundColor(TextColor.ANSI.CYAN); // Highlight for selected unit
                } else {
                    tg.setBackgroundColor(TextColor.ANSI.BLACK);
                }

                String symbol = "O"; // Default for other players
                if (unit.playerId().equals("player1")) {
                    symbol = "X";
                }
                
                tg.setForegroundColor(unit.playerId().equals(PLAYER_ID) ? TextColor.ANSI.GREEN : TextColor.ANSI.RED);
                tg.putString(pos.x() * 2 + 1, pos.y() + 1, " " + symbol);
            }
        }

        tg.setBackgroundColor(TextColor.ANSI.YELLOW);
        tg.setForegroundColor(TextColor.ANSI.BLACK);
        tg.putString(cursorX * 2 + 1, cursorY + 1, "  ");
    }

    static class GameWebSocketListener implements WebSocket.Listener {
        private final StringBuilder buffer = new StringBuilder();

        @Override
        public void onOpen(WebSocket webSocket) {
            status.set("Connection open.");
            webSocket.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            buffer.append(data);
            if (last) {
                String json = buffer.toString();
                if (!json.isBlank()) {
                    try {
                        GameState newState = objectMapper.readValue(json, GameState.class);
                        gameState.set(newState);
                    } catch (MismatchedInputException e) {
                        status.set("Received non-state JSON: " + json);
                    } 
                    catch (IOException e) {
                        status.set("Parse Error: " + e.getMessage());
                    }
                }
                buffer.setLength(0);
            }
            webSocket.request(1);
            return null;
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            status.set("Connection closed: " + reason);
            return null;
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            status.set("WS Error: " + error.getMessage());
        }
    }
}
