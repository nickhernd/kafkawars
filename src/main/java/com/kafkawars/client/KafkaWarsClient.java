package com.kafkawars.client;

import com.kafkawars.domain.GridPosition;
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
    }
}
