package com.kafkawars.domain.events;

import com.kafkawars.domain.GridPosition;

/**
 * Event representing the successful movement of a unit.
 *
 * @param unitId The ID of the unit that moved.
 * @param oldPosition The position the unit moved from.
 * @param newPosition The new position of the unit.
 */
public record UnitMoved(String unitId, GridPosition oldPosition, GridPosition newPosition) implements GameEvent {
}
