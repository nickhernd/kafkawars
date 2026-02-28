package com.kafkawars.domain.events;

public record GameEnded(String matchId, String winnerPlayerId, String winnerUnitId) {}
