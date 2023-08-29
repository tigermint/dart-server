package com.ssh.dartserver.global.config;

import com.ssh.dartserver.global.config.properties.OneSignalProperty;
import com.ssh.dartserver.global.config.properties.SwaggerProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {SwaggerProperty.class, OneSignalProperty.class})
public class PropertiesConfig {
}
