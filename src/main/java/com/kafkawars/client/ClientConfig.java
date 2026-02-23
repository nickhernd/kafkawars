package com.kafkawars.client;

public class ClientConfig {
<<<<<<< HEAD
    public static final String API_BASE_URL = "http://localhost:8082/api/v1";
    public static final String COMMAND_ENDPOINT = API_BASE_URL + "/command";
    public static final String WEBSOCKET_URL = "ws://localhost:8082/ws/game-state";
=======
    private static final String SERVER_IP = System.getProperty("server.ip", "localhost");
    private static final String SERVER_PORT = System.getProperty("server.port", "8081");
    
    public static final String API_BASE_URL = "http://" + SERVER_IP + ":" + SERVER_PORT + "/api/v1";
    public static final String COMMAND_ENDPOINT = API_BASE_URL + "/command";
    public static final String WEBSOCKET_URL = "ws://" + SERVER_IP + ":" + SERVER_PORT + "/ws/game-state";
>>>>>>> 71429a0 (improvement)
}
