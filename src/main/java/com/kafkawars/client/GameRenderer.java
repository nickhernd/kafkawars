package com.kafkawars.client;

// This is a placeholder for the Lanterna-based UI rendering.
// A real implementation would depend on the Lanterna library.
public class GameRenderer {

    public void initialize() {
        System.out.println("GameRenderer: Initializing screen... (Lanterna would do this)");
    }

    public void drawGrid(int width, int height) {
        System.out.println("GameRenderer: Drawing a " + width + "x" + height + " grid.");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print("[ ]");
            }
            System.out.println();
        }
    }

    public void drawUnit(String unitId, int x, int y) {
        System.out.println("GameRenderer: Drawing unit " + unitId + " at (" + x + "," + y + ").");
    }

    public void showMessage(String message) {
        System.out.println("GameRenderer: " + message);
    }

    public void refresh() {
        System.out.println("GameRenderer: Refreshing screen... (Lanterna would do this)");
    }

    public void close() {
        System.out.println("GameRenderer: Closing screen... (Lanterna would do this)");
    }
}
