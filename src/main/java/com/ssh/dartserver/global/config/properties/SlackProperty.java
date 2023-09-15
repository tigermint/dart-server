package com.ssh.dartserver.global.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "slack")
public class SlackProperty {
    private final String url;
    private final Channels channels;

    @Getter
    @RequiredArgsConstructor
    public static class Channels {
        private final String notification;
        private final String idCardVerification;
        private final String inviteMessage;
    }
}
