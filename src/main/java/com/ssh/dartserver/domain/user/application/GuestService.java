package com.ssh.dartserver.domain.user.application;

import com.ssh.dartserver.domain.user.infra.InviteMessageSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuestService {
    private final InviteMessageSender inviteMessageSender;

    public void createGuest(String name, String phoneNumber, String questionContent) {
        inviteMessageSender.sendInviteMessage(name, phoneNumber, questionContent);
    }
}
