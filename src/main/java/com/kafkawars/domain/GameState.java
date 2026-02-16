package com.kafkawars.domain;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents an immutable snapshot of the game state.
 * It contains the state of all units on the board, including who owns them.
 *
 * @param units A map where the key is the unit ID and the value is the unit's state (owner and position).
 */
public record GameState(Map<String, UnitState> units) {

    /**
     * Creates an immutable GameState. The provided map is defensively copied.
     * @param units The map of unit states.
     */
    public GameState(Map<String, UnitState> units) {
        this.units = Collections.unmodifiableMap(Map.copyOf(units));
    }

    /**
     * Returns a new GameState with the updated position for a specific unit.
     * The owner of the unit remains the same.
     *
     * @param unitId The ID of the unit to move.
     * @param newPosition The new position of the unit.
     * @return A new, immutable GameState instance with the change applied.
     */
    public GameState updateUnitPosition(String unitId, GridPosition newPosition) {
        Map<String, UnitState> newUnits = new java.util.HashMap<>(this.units);
        UnitState oldUnitState = newUnits.get(unitId);
        if (oldUnitState != null) {
            newUnits.put(unitId, new UnitState(oldUnitState.playerId(), newPosition));
        }
        return new GameState(newUnits);
    }

    /**
     * Helper method to get a map of just the unit positions.
     * @return A map where the key is the unit ID and the value is the GridPosition.
     */
    public Map<String, GridPosition> getUnitPositions() {
        return units.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().position()));
    }
}
