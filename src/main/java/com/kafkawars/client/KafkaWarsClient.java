package com.kafkawars.client;

import com.kafkawars.domain.GridPosition;
<<<<<<< HEAD
import java.util.Scanner;

public class KafkaWarsClient {

    public static void main(String[] args) throws Exception {
        System.out.println("--- KafkaWars Distributed Client ---");

        CommandSender commandSender = new CommandSender();
        GameRenderer renderer = new GameRenderer();
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Connecting to: " + ClientConfig.API_BASE_URL);
        System.out.print("Enter Player ID: ");
        String playerId = scanner.nextLine();
        String matchId = "match-1";

        renderer.initialize();

        // Background thread to refresh the map periodically
        Thread refreshThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    com.kafkawars.domain.GameState state = commandSender.fetchGameState(matchId);
                    if (state != null) {
                        renderer.render(state.getUnitPositions());
                    }
                    Thread.sleep(1000); // Refresh every 1 second
                }
            } catch (InterruptedException ignored) {
            } catch (Exception e) {
                // Silently ignore refresh errors to not mess up the terminal
            }
        });
        refreshThread.setDaemon(true);
        refreshThread.start();

        System.out.println("CONTROLS: W/A/S/D to move. 'Q' to quit.");
        System.out.println("Your unit: " + (playerId.equals("player1") ? "p1-unit1" : "p2-unit1"));

        String unitId = playerId.equals("player1") ? "p1-unit1" : "p2-unit1";

        while (true) {
            int key = KeyboardHandler.readKey();
            if (key == 'q' || key == 'Q' || key == 3) break; // 3 is Ctrl+C

            try {
                com.kafkawars.domain.GameState state = commandSender.fetchGameState(matchId);
                if (state == null) continue;

                com.kafkawars.domain.GridPosition currentPos = state.getUnitPositions().get(unitId);
                if (currentPos == null) continue;

                int nextX = currentPos.x();
                int nextY = currentPos.y();

                if (key == 'w' || key == 'W') nextY--;
                else if (key == 's' || key == 'S') nextY++;
                else if (key == 'a' || key == 'A') nextX--;
                else if (key == 'd' || key == 'D') nextX++;
                else continue; // Ignore other keys

                // Clamp values to grid bounds (0-19)
                nextX = Math.max(0, Math.min(19, nextX));
                nextY = Math.max(0, Math.min(19, nextY));

                if (nextX != currentPos.x() || nextY != currentPos.y()) {
                    commandSender.sendMoveCommand(playerId, unitId, matchId, new com.kafkawars.domain.GridPosition(nextX, nextY));
                    
                    // Immediate local update for better feel
                    com.kafkawars.domain.GameState newState = commandSender.fetchGameState(matchId);
                    if (newState != null) renderer.render(newState.getUnitPositions());
                }
                
            } catch (Exception e) {
                // renderer.showMessage("Error: " + e.getMessage());
            }
        }

        System.out.println("\n--- KafkaWars Client Shutting Down ---");
=======
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

    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 20;

    private static String PLAYER_ID = "player1";
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
                    if (keyStroke.getKeyType() == KeyType.ArrowUp)    cursorY = Math.max(0, cursorY - 1);
                    if (keyStroke.getKeyType() == KeyType.ArrowDown)  cursorY = Math.min(GRID_HEIGHT - 1, cursorY + 1);
                    if (keyStroke.getKeyType() == KeyType.ArrowLeft)  cursorX = Math.max(0, cursorX - 1);
                    if (keyStroke.getKeyType() == KeyType.ArrowRight) cursorX = Math.min(GRID_WIDTH - 1, cursorX + 1);

                    if (keyStroke.getKeyType() == KeyType.Enter) {
                        final GridPosition cursorPosition = new GridPosition(cursorX, cursorY);
                        GameState currentState = gameState.get();

                        if (selectedUnitId == null) {
                            Optional<String> unitAtCursor = getUnitIdAtPosition(currentState, cursorPosition);
                            if (unitAtCursor.isPresent()) {
                                UnitState unitState = currentState.units().get(unitAtCursor.get());
                                if (unitState != null && unitState.playerId().equals(PLAYER_ID)) {
                                    selectedUnitId = unitAtCursor.get();
                                    status.set("Selected unit: " + selectedUnitId);
                                } else {
                                    status.set("Cannot select opponent's unit.");
                                }
                            }
                        } else {
                            final String unitToMove = selectedUnitId;
                            selectedUnitId = null;
                            try {
                                status.set("Sending move command for " + unitToMove + " to " + cursorPosition);
                                commandSender.sendMoveCommand(PLAYER_ID, unitToMove, MATCH_ID, cursorPosition);
                            } catch (Exception e) {
                                status.set("Error sending command: " + e.getMessage());
                            }
                        }
                    }
                }

                screen.clear();
                final TextGraphics tg = screen.newTextGraphics();

                draw(tg, GRID_WIDTH, GRID_HEIGHT, gameState.get(), cursorX, cursorY, selectedUnitId);

                tg.putString(0, GRID_HEIGHT + 2, "Player: " + PLAYER_ID);
                tg.putString(0, GRID_HEIGHT + 3, "Status: " + status.get());
                tg.putString(0, GRID_HEIGHT + 4, "Cursor: (" + cursorX + "," + cursorY + ")");

                screen.refresh();
                Thread.sleep(1000 / 30);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            screen.close();
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
                    tg.setBackgroundColor(TextColor.ANSI.CYAN);
                } else {
                    tg.setBackgroundColor(TextColor.ANSI.BLACK);
                }

                String symbol = unit.playerId().substring(0, 1).toUpperCase();
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
                    } catch (IOException e) {
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
>>>>>>> 151bd7b (bugs correction)
    }
}
