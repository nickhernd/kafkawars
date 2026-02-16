package com.kafkawars.logic;

import com.kafkawars.domain.GameState;
import com.kafkawars.domain.GridPosition;
import com.kafkawars.domain.MoveCommand;
import com.kafkawars.domain.events.MovementRejected;
import com.kafkawars.domain.events.UnitMoved;

import java.util.Objects;

/**
 * The core game engine, responsible for processing commands and applying business logic.
 * This class is stateless and operates on the given game state.
 */
import org.springframework.stereotype.Service;

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
        // --- Validation Logic ---

        // 1. Check if the target position is already occupied by another unit.
        // This is the core of the deterministic concurrency resolution.
        if (isCellOccupied(currentState, command.target())) {
            return new ProcessingResult.Failure(
                new MovementRejected(command.unitId(), command.target(), "Target cell is occupied.")
            );
        }

        GridPosition oldPosition = currentState.unitPositions().get(command.unitId());

        // A more robust implementation would check for unit existence, movement range, energy, etc.
        // For now, we only check for collisions.

        // --- Event Generation ---
        
        // If all validations pass, create a UnitMoved event.
        UnitMoved event = new UnitMoved(
            command.unitId(),
            Objects.requireNonNullElse(oldPosition, new GridPosition(-1, -1)), // Assuming new units start off-board
            command.target()
        );

        return new ProcessingResult.Success(event);
    }

    private boolean isCellOccupied(GameState gameState, GridPosition targetPosition) {
        return gameState.unitPositions().containsValue(targetPosition);
    }
}
