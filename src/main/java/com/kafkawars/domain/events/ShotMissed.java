package com.kafkawars.domain.events;

public record ShotMissed(String attackerUnitId, String direction) {}
