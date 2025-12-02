package com.aajumaharjan.demofeatures.async;

import java.util.function.Consumer;

public interface AsyncMessagingClient {
    /**
     * Publish any object payload. Implementations are responsible for serialization (typically JSON).
     */
    void publish(String destination, Object payload);

    /**
     * Register a listener and convert to the requested type.
     */
    <T> void registerListener(String destination, Class<T> type, Consumer<T> handler);

    /**
     * Convenience overload when the caller just wants the raw object/map representation.
     */
    default void registerListener(String destination, Consumer<Object> handler) {
        registerListener(destination, Object.class, handler);
    }

    String serialize(Object payload);

    <T> T deserialize(String json, Class<T> type);
}
