package com.kafkawars.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

public class StateReceiver implements WebSocket.Listener {

    private final CountDownLatch latch = new CountDownLatch(1);

    public StateReceiver() {
        HttpClient client = HttpClient.newHttpClient();
        client.newWebSocketBuilder()
              .buildAsync(URI.create(ClientConfig.WEBSOCKET_URL), this);
        System.out.println("WebSocket listener initialized.");
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        System.out.println("WebSocket connection opened.");
        // Request initial state or subscribe to a match
        // webSocket.sendText("SUBSCRIBE:match-1", true);
        WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        System.out.println("Received game state update: " + data);
        // Here, we would parse the data and pass it to the GameRenderer
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        System.out.println("WebSocket connection closed: " + statusCode + " " + reason);
        latch.countDown();
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        System.err.println("WebSocket error: " + error.getMessage());
        WebSocket.Listener.super.onError(webSocket, error);
    }

    public void awaitClose() throws InterruptedException {
        latch.await();
    }
}
