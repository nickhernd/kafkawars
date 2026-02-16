package com.kafkawars.client;

public class ClientConfig {
    public static final String API_BASE_URL = "http://localhost:8082/api/v1";
    public static final String COMMAND_ENDPOINT = API_BASE_URL + "/command";
    public static final String WEBSOCKET_URL = "ws://localhost:8082/ws/game-state";
}
