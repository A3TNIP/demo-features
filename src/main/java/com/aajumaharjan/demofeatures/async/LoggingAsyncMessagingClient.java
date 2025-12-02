package com.aajumaharjan.demofeatures.async;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class LoggingAsyncMessagingClient implements AsyncMessagingClient {
    private static final Logger log = LoggerFactory.getLogger(LoggingAsyncMessagingClient.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private final String backend;
    private final String endpoint;

    public LoggingAsyncMessagingClient(String backend, String endpoint) {
        this.backend = backend;
        this.endpoint = endpoint;
    }

    @Override
    public void publish(String destination, Object payload) {
        log.info("[async:{}:{}] publish -> destination='{}' payload='{}'", backend, endpoint, destination, safeJson(payload));
    }

    @Override
    public <T> void registerListener(String destination, Class<T> type, Consumer<T> handler) {
        log.info("[async:{}:{}] registerListener -> destination='{}' (logging stub)", backend, endpoint, destination);
        // simulate receipt
        handler.accept(type.cast(safeJson("mock-message-from-" + destination)));
    }

    private Object safeJson(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (Exception e) {
            return String.valueOf(o);
        }
    }

    @Override
    public String serialize(Object payload) {
        try {
            return mapper.writeValueAsString(payload);
        } catch (Exception e) {
            log.warn("Failed to serialize payload {}, sending as string: {}", payload, e.getMessage());
            return String.valueOf(payload);
        }
    }

    @Override
    public <T> T deserialize(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            log.warn("Failed to deserialize message to {}: {}", type.getSimpleName(), e.getMessage());
            return type.cast(json);
        }
    }
}
