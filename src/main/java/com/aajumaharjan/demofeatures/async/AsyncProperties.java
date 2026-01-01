package com.aajumaharjan.demofeatures.async;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "plugins.demo-features.async")
public class AsyncProperties {

    /**
     * Allows RabbitMQ async processing to be disabled by host applications if needed.
     */
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
