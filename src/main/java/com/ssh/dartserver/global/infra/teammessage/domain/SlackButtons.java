package com.ssh.dartserver.global.infra.teammessage.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlackButtons {
    private final List<Map<String, Object>> buttons = new ArrayList<>();

    void addButton(String text, String url) {
        Map<String, Object> button = new HashMap<>();
        button.put("type", "button");
        button.put("text", text);
        button.put("url", url);

        buttons.add(button);
    }

    public List<Map<String, Object>> getButtons() {
        return buttons;
    }

    public boolean isEmpty() {
        return buttons.isEmpty();
    }
}
