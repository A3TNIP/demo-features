package com.aajumaharjan.demofeatures.async;

import java.util.function.Consumer;

public interface AsyncMessagingClient {
    void publish(String destination, String payload);

    /**
     * Register a listener for a destination/topic/queue.
     */
    void registerListener(String destination, Consumer<String> handler);
}
