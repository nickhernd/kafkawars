package com.kafkawars.api;

import java.util.List;

public record LobbyState(String matchId, List<String> players, String status) {}
