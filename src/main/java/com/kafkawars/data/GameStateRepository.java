package com.kafkawars.data;

import com.kafkawars.api.LobbyState;
import com.kafkawars.domain.GameState;
import com.kafkawars.domain.GameStatus;
import com.kafkawars.domain.GridPosition;
import com.kafkawars.domain.UnitState;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

@Repository
public class GameStateRepository {

    private static final int[][] CORNERS = {{1, 1}, {18, 1}, {1, 18}, {18, 18}};

    private final Map<String, GameState> gameStates = new ConcurrentHashMap<>();

    // Lobby: single waiting room
    private final AtomicReference<String> lobbyMatchId = new AtomicReference<>(null);
    private final CopyOnWriteArrayList<String> lobbyPlayers = new CopyOnWriteArrayList<>();

    public Optional<GameState> findByMatchId(String matchId) {
        return Optional.ofNullable(gameStates.get(matchId));
    }

<<<<<<< HEAD
    /** Legacy 2-player init for backward compatibility. */
=======
    /**
     * Returns the state for a given match, or throws if not found.
     * @param matchId The ID of the match.
     * @return The GameState.
     * @throws IllegalStateException if no state exists for the match.
     */
    public GameState getByMatchId(String matchId) {
        return findByMatchId(matchId)
                .orElseThrow(() -> new IllegalStateException("No state found for match: " + matchId));
    }

    /**
     * Returns the state for a given match, or throws if not found.
     * @param matchId The ID of the match.
     * @return The GameState.
     * @throws IllegalStateException if no state exists for the match.
     */
    public GameState getByMatchId(String matchId) {
        return findByMatchId(matchId)
                .orElseThrow(() -> new IllegalStateException("No state found for match: " + matchId));
    }

    /**
     * Creates and saves the initial state for a new match.
     * @param matchId The ID of the match to create.
     * @return The newly created GameState.
     */
>>>>>>> 151bd7b (bugs correction)
    public GameState createInitialState(String matchId) {
        return createInitialState(matchId, List.of("player1", "player2"));
    }

    /** Create a match with up to 4 players, each placed at a corner. */
    public GameState createInitialState(String matchId, List<String> playerIds) {
        Map<String, UnitState> units = new HashMap<>();
        int count = Math.min(playerIds.size(), 4);
        for (int i = 0; i < count; i++) {
            String pid = playerIds.get(i);
            String uid = "p" + (i + 1) + "-unit1";
            units.put(uid, new UnitState(pid, new GridPosition(CORNERS[i][0], CORNERS[i][1])));
        }
        GameState initialState = new GameState(units, GameStatus.ACTIVE, null);
        this.save(matchId, initialState);
        return initialState;
    }

    public void save(String matchId, GameState gameState) {
        gameStates.put(matchId, gameState);
    }

    /** Add a player to the lobby. Returns the LobbyState after joining. */
    public synchronized LobbyState joinLobby(String playerId) {
        // Allocate a matchId if this is the first player
        lobbyMatchId.compareAndSet(null, "match-" + UUID.randomUUID().toString().substring(0, 8));
        String matchId = lobbyMatchId.get();

        if (!lobbyPlayers.contains(playerId)) {
            lobbyPlayers.add(playerId);
        }

        if (lobbyPlayers.size() >= 2) {
            // Start the game if not already active
            GameState existing = gameStates.get(matchId);
            if (existing == null || existing.status() == GameStatus.WAITING) {
                createInitialState(matchId, new ArrayList<>(lobbyPlayers));
            }
            List<String> players = new ArrayList<>(lobbyPlayers);
            // Reset lobby for next game
            lobbyMatchId.set(null);
            lobbyPlayers.clear();
            return new LobbyState(matchId, players, "ACTIVE");
        }

        return new LobbyState(matchId, new ArrayList<>(lobbyPlayers), "WAITING");
    }

    /** Get current lobby state. */
    public LobbyState getLobbyState() {
        String matchId = lobbyMatchId.get();
        if (matchId == null) {
            return new LobbyState(null, List.of(), "EMPTY");
        }
        return new LobbyState(matchId, new ArrayList<>(lobbyPlayers), "WAITING");
    }
}
