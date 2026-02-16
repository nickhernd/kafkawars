package com.kafkawars.client;

import com.kafkawars.domain.GridPosition;

import java.io.IOException;

public class KafkaWarsClient {

    public static void main(String[] args) throws Exception {
        System.out.println("--- KafkaWars Client Starting ---");

        // Initialize components
        GameRenderer renderer = new GameRenderer();
        CommandSender commandSender = new CommandSender();
        
        // This would be the WebSocket connection, running in a background thread.
        // For this example, we create it but don't interact with it further.
        StateReceiver stateReceiver = new StateReceiver();

        renderer.initialize();
        renderer.drawGrid(10, 10);
        renderer.refresh();

        // --- Main Game Loop (Conceptual) ---
        // A real TUI client would loop here, waiting for keyboard input.
        // For this example, we just simulate sending one command.

        renderer.showMessage("Pressing 'm' to move unit 'unit-1' to (1,0)...");
        
        // Simulate sending a command
        try {
            commandSender.sendMoveCommand("player-1", "unit-1", "match-1", new GridPosition(1, 0));
        } catch (Exception e) {
            renderer.showMessage("Error sending command: " + e.getMessage());
        }

        renderer.showMessage("Command sent. Waiting for state updates via WebSocket...");
        renderer.refresh();

        // In a real app, the program would not exit here. It would wait for user input
        // or for the WebSocket to close. We use the latch from the receiver to keep it alive.
        System.out.println("Client main thread is now waiting. (Press Ctrl+C to exit)");
        stateReceiver.awaitClose();

        renderer.close();
        System.out.println("--- KafkaWars Client Shutting Down ---");
    }
}
