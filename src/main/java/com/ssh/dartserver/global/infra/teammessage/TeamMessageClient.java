package com.ssh.dartserver.global.infra.teammessage;

import com.ssh.dartserver.global.infra.teammessage.domain.TeamMessage;

public interface TeamMessageClient {
    void send(String channel, String message);
    void send(TeamMessage teamMessage);
}
