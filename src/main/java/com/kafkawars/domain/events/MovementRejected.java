package com.kafkawars.domain.events;

import com.kafkawars.domain.GridPosition;

/**
 * Event representing the rejection of a unit's movement.
 *
 * @param unitId The ID of the unit whose move was rejected.
 * @param attemptedPosition The position the unit tried to move to.
 * @param reason A description of why the movement was rejected.
 */
public record MovementRejected(String unitId, GridPosition attemptedPosition, String reason) implements GameEvent {
}
