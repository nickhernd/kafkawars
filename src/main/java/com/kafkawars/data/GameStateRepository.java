package com.kafkawars.data;

import com.kafkawars.domain.GameState;
import com.kafkawars.domain.GridPosition;
import com.kafkawars.domain.UnitState;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
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
     * @param matchId The ID of the match.
     * @return An Optional containing the GameState if it exists, otherwise empty.
     */
    public Optional<GameState> findByMatchId(String matchId) {
        return Optional.ofNullable(gameStates.get(matchId));
    }

    /**
     * Creates and saves the initial state for a new match.
     * @param matchId The ID of the match to create.
     * @return The newly created GameState.
     */
    public GameState createInitialState(String matchId) {
        GameState initialState = new GameState(Map.of(
            "p1-unit1", new UnitState("player1", new GridPosition(1, 1)),
            "p2-unit1", new UnitState("player2", new GridPosition(8, 8))
        ));
        this.save(matchId, initialState);
        return initialState;
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
