package com.ssh.dartserver.global.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperty {
    private final Info info;

    @Getter
    @RequiredArgsConstructor
    public static class Info {
        private final String title;
        private final String version;
        private final String description;
    }
}
