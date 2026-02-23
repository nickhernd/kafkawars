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

    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 20;
    private static final double MAX_MOVE_DISTANCE = 1.5; // Allows diagonal movement (sqrt(2) approx 1.414)

    /**
     * Processes a single MoveCommand against the current game state.
     * This is a pure function that returns the result of the command without side effects.
     *
     * @param currentState The state of the game before the command.
     * @param command The move command to process.
     * @return A ProcessingResult, which is either a Success (with UnitMoved) or a Failure (with MovementRejected).
     */
    public ProcessingResult processMove(GameState currentState, MoveCommand command) {
<<<<<<< HEAD
<<<<<<< HEAD
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
=======
        // --- Validation Logic ---
        
        // 1. Check if the target position is within the grid boundaries.
        if (!command.target().isValid(GRID_WIDTH, GRID_HEIGHT)) {
            return new ProcessingResult.Failure(
                new MovementRejected(command.unitId(), command.target(), "Target position is out of bounds.")
            );
        }

=======
        // --- Validation Logic ---
        
        // 1. Check if the target position is within the grid boundaries.
        if (!command.target().isValid(GRID_WIDTH, GRID_HEIGHT)) {
            return new ProcessingResult.Failure(
                new MovementRejected(command.unitId(), command.target(), "Target position is out of bounds.")
            );
        }

>>>>>>> 71429a0 (improvement)
        // 2. Check if the target position is already occupied by another unit.
        // This is the core of the deterministic concurrency resolution.
>>>>>>> 71429a0 (improvement)
        if (isCellOccupied(currentState, command.target())) {
            return new ProcessingResult.Failure(
                new MovementRejected(command.unitId(), command.target(), "Target cell is occupied.")
            );
        }

        // A more robust implementation would check for movement range, energy, etc.
        GridPosition oldPosition = unitState.position();

<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
>>>>>>> 71429a0 (improvement)
        // 3. Movement range validation
        if (oldPosition != null) {
            // Unit exists, check if movement is within range.
            if (oldPosition.distanceTo(command.target()) > MAX_MOVE_DISTANCE) {
                return new ProcessingResult.Failure(
                    new MovementRejected(command.unitId(), command.target(), "Movement distance too far.")
                );
            }
        } else {
            // Unit does not exist (Spawning).
            // Logic for spawning can be added here (e.g., spawn zones).
            // For now, we allow spawning anywhere that is valid and unoccupied.
        }

        // --- Event Generation ---
        
>>>>>>> 71429a0 (improvement)
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
