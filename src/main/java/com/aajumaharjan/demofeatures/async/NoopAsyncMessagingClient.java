package com.aajumaharjan.demofeatures.async;

import java.util.function.Consumer;

public class NoopAsyncMessagingClient implements AsyncMessagingClient {
    @Override
    public void publish(String destination, Object payload) {
        // intentionally no-op
    }

    @Override
    public <T> void registerListener(String destination, Class<T> type, Consumer<T> handler) {
        // intentionally no-op
    }

    @Override
    public String serialize(Object payload) {
        // intentionally no-op
        return null;
    }

    @Override
    public <T> T deserialize(String json, Class<T> type) {
        // intentionally no-op
        return null;
    }
}
