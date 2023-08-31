package com.ssh.dartserver.domain.user.infra;

import com.ssh.dartserver.global.config.properties.SlackProperty;
import com.ssh.dartserver.global.infra.teammessage.TeamMessageClient;
import com.ssh.dartserver.global.infra.teammessage.domain.SlackMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
@Component
public class VerifyMessageSender {
    private final TeamMessageClient teamMessageClient;
    private final SlackProperty slackProperty;

    private static final String OKAY_WORD = "✅승인";
    private static final String FAIL_WORD = "❌반려";
    private static final String ADMIN_CHECK_PATH = "/v1/admin/verify-id-card";

    public void sendIdCardVerification(Long userId, String userName, String imageUrl) {
        final String msg = userId + " " + userName;

        String successPath = makePath(userId, "success");
        String failurePath = makePath(userId, "failed");

        SlackMessage slackMessage = new SlackMessage();
        slackMessage.setChannel(slackProperty.getChannels().getIdCardVerification());
        slackMessage.setText(msg);
        slackMessage.addButton(OKAY_WORD, successPath);
        slackMessage.addButton(FAIL_WORD, failurePath);
        slackMessage.setImage(imageUrl);
        teamMessageClient.send(slackMessage);
    }

    private static String makePath(Long userId, String sign) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(ADMIN_CHECK_PATH)
                .queryParam("id", userId)
                .queryParam("sign", sign)
                .toUriString();
    }
}
