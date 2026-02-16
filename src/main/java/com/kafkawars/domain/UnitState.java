package com.kafkawars.domain;

/**
 * Represents the state of a single unit, including its owner and position.
 *
 * @param playerId The ID of the player who owns the unit.
 * @param position The current position of the unit on the grid.
 */
public record UnitState(String playerId, GridPosition position) {
}
