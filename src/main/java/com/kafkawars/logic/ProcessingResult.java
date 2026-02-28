package com.kafkawars.logic;

import com.kafkawars.domain.events.MovementRejected;
import com.kafkawars.domain.events.ShotMissed;
import com.kafkawars.domain.events.UnitHit;
import com.kafkawars.domain.events.UnitMoved;

public sealed interface ProcessingResult {
    record Success(UnitMoved event) implements ProcessingResult {}
    record Failure(MovementRejected event) implements ProcessingResult {}
    record AttackHit(UnitHit event) implements ProcessingResult {}
    record AttackMiss(ShotMissed event) implements ProcessingResult {}
}
