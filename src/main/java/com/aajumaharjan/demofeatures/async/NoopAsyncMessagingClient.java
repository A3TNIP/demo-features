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
}
