package com.ssh.dartserver.global.infra.teammessage.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpEntity;

@Getter
public class SlackMessage implements TeamMessage {
    private String channel;
    private String text;
    private final SlackButtons buttons = new SlackButtons();
    private String imageUrl;

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public void addButton(String text, String url) {
        buttons.addButton(text, url);
    }

    @Override
    public void setImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public HttpEntity<Map<String, Object>> newHttpEntity() {
        validate();
        final Map<String, Object> request = new HashMap<>();

        request.put("channel", channel);
        request.put("text", text);
        request.put("attachments", newAttachments());

        return new HttpEntity<>(request);
    }

    private List<Map<String, Object>> newAttachments() {
        Map<String, Object> attachment = new HashMap<>();
        attachment.put("fallback","버튼을 선택해주세요"); // 지원되지 않는 플랫폼에서 보여줄 메세지
        attachment.put("callback_id","id_card_verification"); // callback_id 액션 처리 시 식별자 역할
        attachment.put("color","#3AA3E3");
        if (!buttons.isEmpty()) {
            attachment.put("actions", buttons.getButtons());
        }
        if (imageUrl != null && !imageUrl.isEmpty()) {
            attachment.put("image_url", imageUrl);
        }
        List<Map<String,Object>> attachments = new ArrayList<>();
        attachments.add(attachment);
        return attachments;
    }

    private void validate() {
        if (channel == null || channel.isEmpty()) {
            throw new IllegalStateException();
        }
        if (text == null || text.isEmpty()) {
            throw new IllegalStateException();
        }
    }
}
