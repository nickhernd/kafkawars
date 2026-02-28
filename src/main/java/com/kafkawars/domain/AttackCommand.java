package com.kafkawars.domain;

public record AttackCommand(
    String actionType,
    String playerId,
    String unitId,
    String matchId,
    String direction
) {}
