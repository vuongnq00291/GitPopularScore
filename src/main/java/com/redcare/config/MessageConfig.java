package com.redcare.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "messages")
@Data
public class MessageConfig {
    private String invalidDateFormat;
}
