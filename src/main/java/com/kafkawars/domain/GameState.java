package com.kafkawars.domain;

import java.util.Collections;
import java.util.Map;

/**
 * Represents an immutable snapshot of the game state.
 * For now, it only contains the positions of all units on the board.
 *
 * @param unitPositions A map where the key is the unit ID and the value is the unit's GridPosition.
 */
public record GameState(Map<String, GridPosition> unitPositions) {

    /**
     * Creates an immutable GameState. The provided map is defensively copied.
     * @param unitPositions The map of unit positions.
     */
    public GameState(Map<String, GridPosition> unitPositions) {
        this.unitPositions = Collections.unmodifiableMap(Map.copyOf(unitPositions));
    }

    /**
     * Returns a new GameState with the updated position for a specific unit.
     *
     * @param unitId The ID of the unit to move.
     * @param newPosition The new position of the unit.
     * @return A new, immutable GameState instance with the change applied.
     */
    public GameState updateUnitPosition(String unitId, GridPosition newPosition) {
        Map<String, GridPosition> newPositions = new java.util.HashMap<>(this.unitPositions);
        newPositions.put(unitId, newPosition);
        return new GameState(newPositions);
    }
}
