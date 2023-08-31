package com.ssh.dartserver.global.infra.teammessage;

import com.ssh.dartserver.global.config.properties.SlackProperty;
import com.ssh.dartserver.global.infra.teammessage.domain.SlackMessage;
import com.ssh.dartserver.global.infra.teammessage.domain.TeamMessage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Component
public class SlackMessageClient implements TeamMessageClient {
    private final RestTemplate restTemplate;
    private final SlackProperty slackProperty;

    @Override
    public void send(String channel, String message) {
        postMessage(channel, message);
    }

    @Override
    public void send(TeamMessage teamMessage) {
        postMessage(teamMessage);
    }

    private void postMessage(String channel, String message) {
        SlackMessage slackMessage = new SlackMessage();
        slackMessage.setChannel(channel);
        slackMessage.setText(message);

        postMessage(slackMessage);
    }

    private void postMessage(TeamMessage teamMessage) {
        HttpEntity<Map<String,Object>> entity = teamMessage.newHttpEntity();
        restTemplate.exchange(slackProperty.getUrl(), HttpMethod.POST, entity,String.class);
    }
}
