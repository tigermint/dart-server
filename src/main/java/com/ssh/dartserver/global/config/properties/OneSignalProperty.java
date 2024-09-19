package com.ssh.dartserver.global.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "onesignal")
public class OneSignalProperty {
    private final String appId;
    private final String restApiKey;
}
