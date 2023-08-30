package com.ssh.dartserver.global.infra.teammessage.domain;

import java.util.Map;
import org.springframework.http.HttpEntity;

public interface TeamMessage {
    void setText(String text);
    void setChannel(String channel);
    void addButton(String text, String url);
    void setImage(String imageUrl);
    HttpEntity<Map<String,Object>> newHttpEntity();
}
