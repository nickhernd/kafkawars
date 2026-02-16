package com.kafkawars.logic;

import com.kafkawars.domain.events.MovementRejected;
import com.kafkawars.domain.events.UnitMoved;

/**
 * A sealed interface to represent the result of processing a command.
 * It can either be a Success, containing a UnitMoved event,
 * or a Failure, containing a MovementRejected event.
 */
public sealed interface ProcessingResult {
    /**
     * A successful processing result.
     * @param event The UnitMoved event that was generated.
     */
    record Success(UnitMoved event) implements ProcessingResult {}

    /**
     * A failed processing result.
     * @param event The MovementRejected event that was generated.
     */
    record Failure(MovementRejected event) implements ProcessingResult {}
}
