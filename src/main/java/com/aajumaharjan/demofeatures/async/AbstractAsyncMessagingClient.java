package com.aajumaharjan.demofeatures.async;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractAsyncMessagingClient implements AsyncMessagingClient {
    public static final ObjectMapper mapper = new ObjectMapper();
    @Override
    public String serialize(Object payload) {
        try {
            return mapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize payload: " + payload, e);
        }
    }

    @Override
    public <T> T deserialize(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize message to " + type.getSimpleName() + ": " + json, e);
        }
    }
}
