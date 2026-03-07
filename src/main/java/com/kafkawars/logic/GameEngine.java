package com.kafkawars.logic;

import com.kafkawars.domain.AttackCommand;
import com.kafkawars.domain.GameState;
import com.kafkawars.domain.GridPosition;
import com.kafkawars.domain.MoveCommand;
import com.kafkawars.domain.UnitState;
import com.kafkawars.domain.events.MovementRejected;
import com.kafkawars.domain.events.ShotMissed;
import com.kafkawars.domain.events.UnitHit;
import com.kafkawars.domain.events.UnitMoved;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class GameEngine {

    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 20;
    private static final double MAX_MOVE_DISTANCE = 1.5;

    public ProcessingResult processMove(GameState currentState, MoveCommand command) {
<<<<<<< HEAD
=======

        // 1. Check if target position is within the grid boundaries.
>>>>>>> 151bd7b (bugs correction)
        if (!command.target().isValid(GRID_WIDTH, GRID_HEIGHT)) {
            return new ProcessingResult.Failure(
                new MovementRejected(command.unitId(), command.target(), "Target position is out of bounds.")
            );
        }

        UnitState unitState = currentState.units().get(command.unitId());

<<<<<<< HEAD
=======
        // 2. Check if the unit exists.
>>>>>>> 151bd7b (bugs correction)
        if (unitState == null) {
            return new ProcessingResult.Failure(
                new MovementRejected(command.unitId(), command.target(), "Unit does not exist.")
            );
        }

<<<<<<< HEAD
=======
        // 3. Check if the player owns the unit.
>>>>>>> 151bd7b (bugs correction)
        if (!unitState.playerId().equals(command.playerId())) {
            return new ProcessingResult.Failure(
                new MovementRejected(command.unitId(), command.target(), "Player does not own this unit.")
            );
        }

<<<<<<< HEAD
=======
        // 4. Check if the target position is already occupied by another unit.
>>>>>>> 151bd7b (bugs correction)
        if (isCellOccupied(currentState, command.target())) {
            return new ProcessingResult.Failure(
                new MovementRejected(command.unitId(), command.target(), "Target cell is occupied.")
            );
        }

        GridPosition oldPosition = unitState.position();
<<<<<<< HEAD
=======

        // 5. Check movement range.
>>>>>>> 151bd7b (bugs correction)
        if (oldPosition != null && oldPosition.distanceTo(command.target()) > MAX_MOVE_DISTANCE) {
            return new ProcessingResult.Failure(
                new MovementRejected(command.unitId(), command.target(), "Movement distance too far.")
            );
        }

        UnitMoved event = new UnitMoved(
            command.unitId(),
            Objects.requireNonNullElse(oldPosition, new GridPosition(-1, -1)),
            command.target()
        );
        return new ProcessingResult.Success(event);
    }

    public ProcessingResult processAttack(GameState currentState, AttackCommand command) {
        UnitState attacker = currentState.units().get(command.unitId());
        if (attacker == null || !attacker.playerId().equals(command.playerId())) {
            return new ProcessingResult.AttackMiss(new ShotMissed(command.unitId(), command.direction()));
        }

        int dx = 0, dy = 0;
        switch (command.direction()) {
            case "UP"    -> dy = -1;
            case "DOWN"  -> dy =  1;
            case "LEFT"  -> dx = -1;
            case "RIGHT" -> dx =  1;
            default -> {
                return new ProcessingResult.AttackMiss(new ShotMissed(command.unitId(), command.direction()));
            }
        }

        GridPosition current = attacker.position();
        while (true) {
            current = new GridPosition(current.x() + dx, current.y() + dy);
            if (!current.isValid(GRID_WIDTH, GRID_HEIGHT)) {
                return new ProcessingResult.AttackMiss(new ShotMissed(command.unitId(), command.direction()));
            }

            final GridPosition pos = current;
            Optional<Map.Entry<String, UnitState>> hit = currentState.units().entrySet().stream()
                .filter(e -> !e.getKey().equals(command.unitId()))
                .filter(e -> e.getValue().position().equals(pos))
                .findFirst();

            if (hit.isPresent()) {
                UnitState target = hit.get().getValue();
                int newHp = Math.max(0, target.hp() - 1);
                return new ProcessingResult.AttackHit(
                    new UnitHit(command.unitId(), hit.get().getKey(), pos, newHp)
                );
            }
        }
    }

    private boolean isCellOccupied(GameState gameState, GridPosition targetPosition) {
        return gameState.units().values().stream()
<<<<<<< HEAD
            .anyMatch(u -> u.position().equals(targetPosition));
=======
                .anyMatch(unitState -> targetPosition.equals(unitState.position()));
>>>>>>> 151bd7b (bugs correction)
    }
}
