package com.kafkawars.domain;

public record UnitState(String playerId, GridPosition position, int hp, int maxHp) {

    public UnitState(String playerId, GridPosition position) {
        this(playerId, position, 5, 5);
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public UnitState takeDamage(int dmg) {
        return new UnitState(playerId, position, Math.max(0, hp - dmg), maxHp);
    }
}
