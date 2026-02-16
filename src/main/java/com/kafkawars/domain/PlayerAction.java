package com.kafkawars.domain;

/**
 * A marker interface for all actions a player can take.
 * This will be useful for serialization and processing.
 */
public interface PlayerAction {
    String playerId();
    String unitId();
    String actionType();
}
