package com.aajumaharjan.demofeatures.async;

import java.util.function.Consumer;

public class NoopAsyncMessagingClient implements AsyncMessagingClient {
    @Override
    public void publish(String destination, String payload) {
        // intentionally no-op
    }

    @Override
    public void registerListener(String destination, Consumer<String> handler) {
        // intentionally no-op
    }
}
