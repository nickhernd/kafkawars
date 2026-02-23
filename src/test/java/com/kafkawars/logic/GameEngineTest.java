package com.kafkawars.logic;

import com.kafkawars.domain.GameState;
import com.kafkawars.domain.GridPosition;
import com.kafkawars.domain.MoveCommand;
import com.kafkawars.domain.UnitState;
import com.kafkawars.domain.events.MovementRejected;
import com.kafkawars.domain.events.UnitMoved;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameEngineTest {

    private GameEngine gameEngine;

    @BeforeEach
    void setUp() {
        gameEngine = new GameEngine();
    }

    @Test
    void processMove_shouldSucceed_whenTargetCellIsEmpty() {
        // Arrange
        GridPosition initialPos = new GridPosition(0, 0);
        UnitState unit = new UnitState("player-1", initialPos);
        GameState currentState = new GameState(Collections.singletonMap("unit-1", unit));
        MoveCommand command = new MoveCommand("player-1", "unit-1", "match-1", new GridPosition(1, 0), "MOVE");

        // Act
        ProcessingResult result = gameEngine.processMove(currentState, command);

        // Assert
        assertTrue(result instanceof ProcessingResult.Success, "Result should be a success.");
        ProcessingResult.Success successResult = (ProcessingResult.Success) result;
        UnitMoved event = successResult.event();

        assertEquals("unit-1", event.unitId());
        assertEquals(initialPos, event.oldPosition());
        assertEquals(command.target(), event.newPosition());
    }

    @Test
    void processMove_shouldFail_whenTargetCellIsOccupied() {
        // Arrange
        GridPosition unit1Pos = new GridPosition(0, 0);
        GridPosition unit2Pos = new GridPosition(1, 0);
        GameState currentState = new GameState(Map.of(
            "unit-1", new UnitState("player-1", unit1Pos),
            "unit-2", new UnitState("player-2", unit2Pos)
        ));
        
        // Command to move unit-1 into unit-2's position
        MoveCommand command = new MoveCommand("player-1", "unit-1", "match-1", unit2Pos, "MOVE");

        // Act
        ProcessingResult result = gameEngine.processMove(currentState, command);

        // Assert
        assertTrue(result instanceof ProcessingResult.Failure, "Result should be a failure.");
        ProcessingResult.Failure failureResult = (ProcessingResult.Failure) result;
        MovementRejected event = failureResult.event();

        assertEquals("unit-1", event.unitId());
        assertEquals(unit2Pos, event.attemptedPosition());
        assertEquals("Target cell is occupied.", event.reason());
    }

    @Test
    void processMove_shouldFail_ifPlayerDoesNotOwnUnit() {
        // Arrange
        GridPosition initialPos = new GridPosition(0, 0);
        UnitState unit = new UnitState("player-2", initialPos); // This unit is owned by player-2
        GameState currentState = new GameState(Collections.singletonMap("unit-1", unit));
        
        // player-1 tries to move player-2's unit
        MoveCommand command = new MoveCommand("player-1", "unit-1", "match-1", new GridPosition(1, 0), "MOVE");

        // Act
        ProcessingResult result = gameEngine.processMove(currentState, command);

        // Assert
        assertTrue(result instanceof ProcessingResult.Failure, "Result should be a failure.");
    }

    @Test
    void processMove_shouldFail_whenTargetIsOutOfBounds() {
        // Arrange
        GameState currentState = new GameState(Collections.emptyMap());
        // Attempt to spawn outside the 20x20 grid
        MoveCommand command = new MoveCommand("player-1", "bad-unit", "match-1", new GridPosition(20, 20), "MOVE");

        // Act
        ProcessingResult result = gameEngine.processMove(currentState, command);

        // Assert
        assertTrue(result instanceof ProcessingResult.Failure);
        ProcessingResult.Failure failure = (ProcessingResult.Failure) result;
        assertEquals("Target position is out of bounds.", failure.event().reason());
    }

    @Test
    void processMove_shouldFail_whenMovementIsTooFar() {
        // Arrange
        GridPosition initialPos = new GridPosition(0, 0);
        GameState currentState = new GameState(Collections.singletonMap("unit-1", initialPos));
        // Attempt to move 2 steps
        MoveCommand command = new MoveCommand("player-1", "unit-1", "match-1", new GridPosition(2, 0), "MOVE");

        // Act
        ProcessingResult result = gameEngine.processMove(currentState, command);

        // Assert
        assertTrue(result instanceof ProcessingResult.Failure);
        ProcessingResult.Failure failure = (ProcessingResult.Failure) result;
        assertEquals("Movement distance too far.", failure.event().reason());
    }
}
