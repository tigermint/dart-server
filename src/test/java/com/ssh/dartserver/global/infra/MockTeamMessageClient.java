package com.ssh.dartserver.global.infra;

import com.ssh.dartserver.global.infra.teammessage.TeamMessageClient;
import com.ssh.dartserver.global.infra.teammessage.domain.TeamMessage;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class MockTeamMessageClient implements TeamMessageClient {
    @Override
    public void send(final String channel, final String message) {
        System.out.println("TeamMessage Send: " + channel + " " + message);
    }

    @Override
    public void send(final TeamMessage teamMessage) {
        System.out.println("TeamMessage Send: " + teamMessage);
    }
}
