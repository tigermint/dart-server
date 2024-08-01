package com.ssh.dartserver.domain.user.application;

import com.ssh.dartserver.domain.friend.application.FriendDeleter;
import com.ssh.dartserver.domain.survey.application.AnswerUpdater;
import com.ssh.dartserver.domain.survey.application.CommentUpdater;
import com.ssh.dartserver.domain.team.application.TeamDeleter;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserDeleter {
    private final FriendDeleter friendDeleter;
    private final TeamDeleter teamDeleter;
    private final CommentUpdater commentUpdater;
    private final AnswerUpdater answerUpdater;

    private final UserRepository userRepository;

    @Transactional
    public void delete(User user) {
        friendDeleter.deleteAllFriendShip(user);

        teamDeleter.deleteAllTeamAndRelatedData(user);

        //TODO: 커뮤니티 기능 제거 예정
        answerUpdater.updateAllUserToNullInAnswer(user);
        commentUpdater.updateAllUserToNullInCommentAndRelatedData(user);

        //TODO: Soft Delete 적용 예정
        userRepository.delete(user);
    }
}
