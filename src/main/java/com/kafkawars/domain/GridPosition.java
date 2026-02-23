package com.kafkawars.domain;

/**
 * Represents an immutable position on the 2D grid.
 *
 * @param x The x-coordinate.
 * @param y The y-coordinate.
 */
public record GridPosition(int x, int y) {

    public double distanceTo(GridPosition other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    public boolean isValid(int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
