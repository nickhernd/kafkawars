package com.kafkawars.client;

import com.kafkawars.domain.GridPosition;
import java.util.Map;

public class GameRenderer {

    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;

    public void initialize() {
        System.out.println("\033[H\033[2J"); // Clear screen
        System.out.flush();
    }

    public void render(Map<String, GridPosition> unitPositions) {
        StringBuilder sb = new StringBuilder();
        sb.append("\033[H"); // Move cursor to top
        sb.append("=== KAFKA WARS BATTLEFIELD (20x20) ===\n");
        
        // Top border
        sb.append("   ");
        for (int x = 0; x < WIDTH; x++) sb.append(String.format("%2d", x));
        sb.append("\n  +").append("--".repeat(WIDTH)).append("+\n");

        for (int y = 0; y < HEIGHT; y++) {
            sb.append(String.format("%2d|", y));
            for (int x = 0; x < WIDTH; x++) {
                String unitAtPos = findUnitAt(unitPositions, x, y);
                if (unitAtPos != null) {
                    // Use first letter of Unit ID
                    sb.append(" ").append(unitAtPos.substring(0, 1).toUpperCase());
                } else {
                    sb.append(" .");
                }
            }
            sb.append("|\n");
        }

        sb.append("  +").append("--".repeat(WIDTH)).append("+\n");
        sb.append("Units: ").append(unitPositions.toString()).append("\n");
        sb.append("Enter command (unitId x y) or 'exit': ");
        
        System.out.print(sb.toString());
        System.out.flush();
    }

    private String findUnitAt(Map<String, GridPosition> unitPositions, int x, int y) {
        for (Map.Entry<String, GridPosition> entry : unitPositions.entrySet()) {
            if (entry.getValue().x() == x && entry.getValue().y() == y) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void showMessage(String message) {
        System.out.println("\n[LOG]: " + message);
    }

    public void close() {
        System.out.println("Closing game...");
    }
}
