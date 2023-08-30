package com.ssh.dartserver.global.config;

import com.ssh.dartserver.global.config.properties.SlackProperty;
import com.ssh.dartserver.global.config.properties.SwaggerProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {SwaggerProperty.class, SlackProperty.class})
public class PropertiesConfig {
}
