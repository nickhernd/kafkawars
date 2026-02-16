package com.kafkawars.domain;

/**
 * Represents an immutable position on the 2D grid.
 *
 * @param x The x-coordinate.
 * @param y The y-coordinate.
 */
public record GridPosition(int x, int y) {
}
