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
        Map<String, UnitState> initialUnits = new java.util.HashMap<>();
        initialUnits.put("unit-1", new UnitState("p1", new GridPosition(0, 0)));
        
        GameState initialState = new GameState(initialUnits);

        // Try to modify the map passed to the constructor
        initialUnits.put("unit-2", new UnitState("p2", new GridPosition(1, 1)));
        
        assertEquals(1, initialState.units().size(), "Initial GameState should not be affected by external modifications.");
        assertThrows(UnsupportedOperationException.class, () -> {
            initialState.units().put("unit-3", new UnitState("p3", new GridPosition(2, 2)));
        }, "The map returned by units() should be unmodifiable.");
    }

    @Test
    void updateUnitPositionShouldReturnNewGameState() {
        GridPosition initialPos = new GridPosition(5, 5);
        UnitState initialUnit = new UnitState("p1", initialPos);
        GameState initialState = new GameState(Collections.singletonMap("unit-x", initialUnit));

        GridPosition newPos = new GridPosition(6, 5);
        GameState newState = initialState.updateUnitPosition("unit-x", newPos);

        // Verify the new state is different and has the updated position
        assertNotSame(initialState, newState, "updateUnitPosition should return a new instance.");
        assertEquals(newPos, newState.units().get("unit-x").position());

        // Verify the original state is unchanged
        assertEquals(initialPos, initialState.units().get("unit-x").position(), "The original GameState instance should not be mutated.");
    }
}
