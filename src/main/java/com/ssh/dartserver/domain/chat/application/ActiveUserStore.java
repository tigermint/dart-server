package com.ssh.dartserver.domain.chat.application;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ActiveUserStore {
    private final ConcurrentHashMap<String, String> activeSessions = new ConcurrentHashMap<>();
    public void storeSession(String sessionId, String username) {
        activeSessions.put(sessionId, username);
    }

    public void removeSession(String sessionId) {
        activeSessions.remove(sessionId);
    }

    public boolean isUserActive(String username) {
        return activeSessions.containsValue(username);
    }
}
