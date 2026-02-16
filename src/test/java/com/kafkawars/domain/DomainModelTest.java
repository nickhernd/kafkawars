package com.kafkawars.domain;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class DomainModelTest {

    @Test
    void gridPositionShouldHoldCoordinates() {
        GridPosition pos = new GridPosition(10, -5);
        assertEquals(10, pos.x());
        assertEquals(-5, pos.y());
    }

    @Test
    void moveCommandShouldHoldData() {
        GridPosition target = new GridPosition(3, 4);
        MoveCommand command = new MoveCommand("player-1", "unit-alpha", "match-1", target, "MOVE");

        assertEquals("player-1", command.playerId());
        assertEquals("unit-alpha", command.unitId());
        assertEquals("match-1", command.matchId());
        assertEquals(target, command.target());
        assertEquals("MOVE", command.actionType());
    }

    @Test
    void gameStateShouldBeImmutable() {
        Map<String, GridPosition> initialPositions = new java.util.HashMap<>();
        initialPositions.put("unit-1", new GridPosition(0, 0));
        
        GameState initialState = new GameState(initialPositions);

        // Try to modify the map passed to the constructor
        initialPositions.put("unit-2", new GridPosition(1, 1));
        
        assertEquals(1, initialState.unitPositions().size(), "Initial GameState should not be affected by external modifications.");
        assertThrows(UnsupportedOperationException.class, () -> {
            initialState.unitPositions().put("unit-3", new GridPosition(2, 2));
        }, "The map returned by unitPositions() should be unmodifiable.");
    }

    @Test
    void updateUnitPositionShouldReturnNewGameState() {
        GridPosition initialPos = new GridPosition(5, 5);
        GameState initialState = new GameState(Collections.singletonMap("unit-x", initialPos));

        GridPosition newPos = new GridPosition(6, 5);
        GameState newState = initialState.updateUnitPosition("unit-x", newPos);

        // Verify the new state is different and has the updated position
        assertNotSame(initialState, newState, "updateUnitPosition should return a new instance.");
        assertEquals(newPos, newState.unitPositions().get("unit-x"));

        // Verify the original state is unchanged
        assertEquals(initialPos, initialState.unitPositions().get("unit-x"), "The original GameState instance should not be mutated.");
    }
}
