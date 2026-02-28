package com.kafkawars.domain.events;

import com.kafkawars.domain.GridPosition;

public record UnitHit(String attackerUnitId, String targetUnitId, GridPosition hitPosition, int remainingHp) {}
