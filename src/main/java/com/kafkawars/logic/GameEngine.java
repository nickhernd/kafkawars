package com.kafkawars.logic;

import com.kafkawars.domain.GameState;
import com.kafkawars.domain.GridPosition;
import com.kafkawars.domain.MoveCommand;
import com.kafkawars.domain.UnitState;
import com.kafkawars.domain.events.MovementRejected;
import com.kafkawars.domain.events.UnitMoved;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * The core game engine, responsible for processing commands and applying business logic.
 * This class is stateless and operates on the given game state.
 */
@Service
public class GameEngine {

    /**
     * Processes a single MoveCommand against the current game state.
     * This is a pure function that returns the result of the command without side effects.
     *
     * @param currentState The state of the game before the command.
     * @param command The move command to process.
     * @return A ProcessingResult, which is either a Success (with UnitMoved) or a Failure (with MovementRejected).
     */
    public ProcessingResult processMove(GameState currentState, MoveCommand command) {
        UnitState unitState = currentState.units().get(command.unitId());

        // 1. Check if unit exists
        if (unitState == null) {
            return new ProcessingResult.Failure(
                new MovementRejected(command.unitId(), command.target(), "Unit does not exist.")
            );
        }

        // 2. Check if player owns the unit
        if (!unitState.playerId().equals(command.playerId())) {
            return new ProcessingResult.Failure(
                new MovementRejected(command.unitId(), command.target(), "Player does not own this unit.")
            );
        }

        // 3. Check if the target position is already occupied by another unit.
        if (isCellOccupied(currentState, command.target())) {
            return new ProcessingResult.Failure(
                new MovementRejected(command.unitId(), command.target(), "Target cell is occupied.")
            );
        }

        // A more robust implementation would check for movement range, energy, etc.
        GridPosition oldPosition = unitState.position();

        // If all validations pass, create a UnitMoved event.
        UnitMoved event = new UnitMoved(
            command.unitId(),
            Objects.requireNonNullElse(oldPosition, new GridPosition(-1, -1)), // Assuming new units start off-board
            command.target()
        );

        return new ProcessingResult.Success(event);
    }

    private boolean isCellOccupied(GameState gameState, GridPosition targetPosition) {
        return gameState.units().values().stream()
                .anyMatch(unitState -> unitState.position().equals(targetPosition));
    }
}
