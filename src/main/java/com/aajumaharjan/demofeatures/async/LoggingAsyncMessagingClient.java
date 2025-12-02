package com.aajumaharjan.demofeatures.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class LoggingAsyncMessagingClient implements AsyncMessagingClient {
    private static final Logger log = LoggerFactory.getLogger(LoggingAsyncMessagingClient.class);
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
