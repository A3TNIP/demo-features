package com.aajumaharjan.demofeatures.async;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "plugins.demo-features.async")
public class AsyncProperties {

    public enum Provider {LOGGING, NOOP, RABBITMQ, KAFKA}

    private Provider provider = Provider.LOGGING;
    private String endpoint = "default";
    /**
     * Optional fully qualified class name implementing AsyncMessagingClient. If present, overrides provider.
     */
    private String clientClass;

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public void setProvider(String provider) {
        try {
            this.provider = Provider.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid async provider: " + provider + ". Allowed: LOGGING, NOOP, RABBITMQ, KAFKA, NATS");
        }
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getClientClass() {
        return clientClass;
    }

    public void setClientClass(String clientClass) {
        this.clientClass = clientClass;
    }
}
