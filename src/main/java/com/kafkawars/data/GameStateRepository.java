package com.kafkawars.data;

import com.kafkawars.domain.GameState;
import com.kafkawars.domain.GridPosition;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository for storing game state.
 * This is a simplified stand-in for a more robust solution like Kafka Streams KTable or a persistent snapshot store.
 * The key is the Match ID.
 */
@Repository
public class GameStateRepository {

    private final Map<String, GameState> gameStates = new ConcurrentHashMap<>();

    /**
     * Finds the current state for a given match.
     * If no state exists, returns an empty GameState.
     * @param matchId The ID of the match.
     * @return The current GameState.
     */
    public GameState findByMatchId(String matchId) {
        return gameStates.getOrDefault(matchId, new GameState(Collections.emptyMap()));
    }

    /**
     * Saves the new state for a given match.
     * @param matchId The ID of the match.
     * @param gameState The new GameState to save.
     */
    public void save(String matchId, GameState gameState) {
        gameStates.put(matchId, gameState);
    }
}
