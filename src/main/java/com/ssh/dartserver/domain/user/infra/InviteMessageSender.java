package com.ssh.dartserver.domain.user.infra;

import com.ssh.dartserver.global.config.properties.SlackProperty;
import com.ssh.dartserver.global.infra.teammessage.TeamMessageClient;
import com.ssh.dartserver.global.infra.teammessage.domain.SlackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class InviteMessageSender {
    private final TeamMessageClient teamMessageClient;
    private final SlackProperty slackProperty;

    public void sendInviteMessage(String name, String phoneNumber, String questionContent) {
        final String msg = "[" + name + " " + phoneNumber + "]\n친구가 당신을 '" + questionContent + "'으로 선택했어요! 누가 뽑았는지 엔대생 앱에서 확인해봐요! 스토어에서 다운받기! https://dart.page.link/TG78";
        SlackMessage slackMessage = new SlackMessage();
        slackMessage.setChannel(slackProperty.getChannels().getInviteMessage());
        slackMessage.setText(msg);
        teamMessageClient.send(slackMessage);
    }
}
