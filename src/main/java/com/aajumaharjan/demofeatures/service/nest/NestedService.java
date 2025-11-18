package com.aajumaharjan.demofeatures.service.nest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NestedService {
    @Value("${nested.value}")
    private String nestedValue;

    public String getNestedValue() {
        return nestedValue;
    }
}
