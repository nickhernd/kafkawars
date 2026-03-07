package com.kafkawars.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public record GameState(Map<String, UnitState> units, GameStatus status, String winnerId) {

    public GameState(Map<String, UnitState> units, GameStatus status, String winnerId) {
        this.units = Collections.unmodifiableMap(Map.copyOf(units));
        this.status = status;
        this.winnerId = winnerId;
    }

    public GameState(Map<String, UnitState> units) {
        this(units, GameStatus.WAITING, null);
    }

    public GameState updateUnitPosition(String unitId, GridPosition newPosition) {
        Map<String, UnitState> newUnits = new HashMap<>(this.units);
<<<<<<< HEAD
<<<<<<< HEAD
        UnitState old = newUnits.get(unitId);
        if (old != null) {
            newUnits.put(unitId, new UnitState(old.playerId(), newPosition, old.hp(), old.maxHp()));
=======
=======
>>>>>>> 151bd7b (bugs correction)
        UnitState oldUnitState = newUnits.get(unitId);
        if (oldUnitState != null) {
            newUnits.put(unitId, new UnitState(oldUnitState.playerId(), newPosition));
>>>>>>> 151bd7b (bugs correction)
        }
        return new GameState(newUnits, this.status, this.winnerId);
    }

    public GameState applyHit(String unitId) {
        Map<String, UnitState> newUnits = new HashMap<>(this.units);
        UnitState unit = newUnits.get(unitId);
        if (unit == null) return this;

        UnitState damaged = unit.takeDamage(1);
        if (damaged.isAlive()) {
            newUnits.put(unitId, damaged);
        } else {
            newUnits.remove(unitId);
        }
        return new GameState(newUnits, this.status, this.winnerId).checkWinCondition();
    }

    public GameState checkWinCondition() {
        if (this.status != GameStatus.ACTIVE) return this;

        long alivePlayers = units.values().stream()
            .map(UnitState::playerId)
            .distinct()
            .count();

        if (alivePlayers <= 1) {
            String winner = units.values().stream()
                .map(UnitState::playerId)
                .findFirst()
                .orElse(null);
            return new GameState(this.units, GameStatus.FINISHED, winner);
        }
        return this;
    }

    public GameState activate() {
        return new GameState(this.units, GameStatus.ACTIVE, null);
    }

    public Map<String, GridPosition> getUnitPositions() {
        return units.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().position()));
    }
}
