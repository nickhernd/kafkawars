package com.kafkawars.domain;

/**
 * Represents a specific command to move a unit to a target position.
 * This is an immutable data carrier.
 *
 * @param playerId The ID of the player initiating the move.
 * @param unitId The ID of the unit to be moved.
 * @param target The target GridPosition for the unit.
 */
public record MoveCommand(String playerId, String unitId, String matchId, GridPosition target, String actionType) {
}
