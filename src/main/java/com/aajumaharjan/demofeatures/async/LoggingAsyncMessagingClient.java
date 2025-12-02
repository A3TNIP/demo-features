package com.aajumaharjan.demofeatures.async;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;

@Slf4j
public class LoggingAsyncMessagingClient implements AsyncMessagingClient {
    private final String backend;
    private final String endpoint;

    public LoggingAsyncMessagingClient(String backend, String endpoint) {
        this.backend = backend;
        this.endpoint = endpoint;
    }

    @Override
    public void publish(String destination, String payload) {
        log.info("[async:{}:{}] publish -> destination='{}' payload='{}'", backend, endpoint, destination, payload);
    }

    @Override
    public void registerListener(String destination, Consumer<String> handler) {
        log.info("[async:{}:{}] registerListener -> destination='{}' (logging stub)", backend, endpoint, destination);
        // simulate receipt
        handler.accept("mock-message-from-" + destination);
    }
}
