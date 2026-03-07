package com.kafkawars.client;

public class ClientConfig {
    private static final String SERVER_IP = System.getProperty("server.ip", "localhost");
<<<<<<< HEAD
<<<<<<< HEAD
    private static final String SERVER_PORT = System.getProperty("server.port", "8082");
    
=======
    private static final String SERVER_PORT = System.getProperty("server.port", "8081");

>>>>>>> 151bd7b (bugs correction)
=======
    private static final String SERVER_PORT = System.getProperty("server.port", "8081");

>>>>>>> 151bd7b (bugs correction)
    public static final String API_BASE_URL = "http://" + SERVER_IP + ":" + SERVER_PORT + "/api/v1";
    public static final String COMMAND_ENDPOINT = API_BASE_URL + "/command";
    public static final String WEBSOCKET_URL = "ws://" + SERVER_IP + ":" + SERVER_PORT + "/ws/game-state";
}
