package com.ssh.dartserver.domain.user.service;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.dto.UserNextVoteResponse;
import com.ssh.dartserver.domain.user.dto.mapper.UserMapper;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.global.infra.notification.PlatformNotification;
import com.ssh.dartserver.global.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NextVoteService {
    private static final int NEXT_VOTE_AVAILABLE_MINUTES = 40;
    private static final String NEXT_VOTE_AVAILABLE_CONTENTS = "새로운 투표가 가능합니다. 엔대생으로 돌아와주세요!";

    private final UserRepository userRepository;

    private final UserMapper userMapper;
    private final PlatformNotification notification;

    public UserNextVoteResponse readNextVoteAvailableDateTime(User user) {
        return userMapper.toUserNextVoteResponseDto(user.getNextVoteAvailableDateTime().getValue());
    }

    @Transactional
    public synchronized UserNextVoteResponse updateNextVoteAvailableDateTime(User user) {
        User pickingUser = userRepository.findByIdForUpdate(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        pickingUser.updateNextVoteAvailableDateTime(NEXT_VOTE_AVAILABLE_MINUTES);

        CompletableFuture.runAsync(() ->
            notification.postNotificationNextVoteAvailableDateTime(
                    user.getId(),
                    DateTimeUtil.toUTC(user.getNextVoteAvailableDateTime().getValue()),
                    NEXT_VOTE_AVAILABLE_CONTENTS)
        );
        return userMapper.toUserNextVoteResponseDto(pickingUser.getNextVoteAvailableDateTime().getValue());
    }
}
