package com.ssh.dartserver.domain.user.service;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.dto.UserNextVoteResponse;
import com.ssh.dartserver.domain.user.dto.mapper.UserMapper;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.global.infra.notification.PlatformNotification;
import com.ssh.dartserver.global.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public UserNextVoteResponse updateNextVoteAvailableDateTime(User user) {
        user.updateNextVoteAvailableDateTime(NEXT_VOTE_AVAILABLE_MINUTES);
        userRepository.save(user);
        notification.postNotificationNextVoteAvailableDateTime(user.getId(), DateTimeUtils.toUTC(user.getNextVoteAvailableDateTime().getValue()), NEXT_VOTE_AVAILABLE_CONTENTS);
        return userMapper.toUserNextVoteResponseDto(user.getNextVoteAvailableDateTime().getValue());
    }
}
