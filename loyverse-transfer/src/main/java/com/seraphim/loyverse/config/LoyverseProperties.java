package com.seraphim.loyverse.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "loyverse")
public class LoyverseProperties {
    private Source source;
    private Destination destination;
    private Api api;

    @Data
    public static class Source {
        private String token;
    }

    @Data
    public static class Destination {
        private String token;
    }

    @Data
    public static class Api {
        private String url;
    }
}
