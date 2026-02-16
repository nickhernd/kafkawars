package com.kafkawars.logic;

import com.kafkawars.domain.GameState;
import com.kafkawars.domain.GridPosition;
import com.kafkawars.domain.MoveCommand;
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
        GameState currentState = new GameState(Collections.singletonMap("unit-1", initialPos));
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
        GameState currentState = new GameState(Map.of("unit-1", unit1Pos, "unit-2", unit2Pos));
        
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
    void processMove_shouldSucceed_forNewUnit() {
        // A unit that is not yet on the board
        // Arrange
        GameState currentState = new GameState(Collections.emptyMap());
        MoveCommand command = new MoveCommand("player-1", "new-unit", "match-1", new GridPosition(5, 5), "MOVE");

        // Act
        ProcessingResult result = gameEngine.processMove(currentState, command);

        // Assert
        assertTrue(result instanceof ProcessingResult.Success);
        ProcessingResult.Success successResult = (ProcessingResult.Success) result;
        UnitMoved event = successResult.event();

        assertEquals("new-unit", event.unitId());
        // Special position for a unit not previously on the board
        assertEquals(new GridPosition(-1, -1), event.oldPosition());
        assertEquals(new GridPosition(5, 5), event.newPosition());
    }
}
