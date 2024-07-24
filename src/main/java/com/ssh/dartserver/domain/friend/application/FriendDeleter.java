package com.ssh.dartserver.domain.friend.application;

import com.ssh.dartserver.domain.friend.infra.FriendRepository;
import com.ssh.dartserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FriendDeleter {

    private final FriendRepository friendRepository;

    @Transactional
    public void deleteAllFriendShip(User user) {
        friendRepository.deleteAllInBatchByUserOrFriendUser(user, user);
    }

}
