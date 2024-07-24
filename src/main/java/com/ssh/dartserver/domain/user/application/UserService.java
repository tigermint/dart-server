package com.ssh.dartserver.domain.user.application;

import com.ssh.dartserver.domain.friend.application.FriendDeleter;
import com.ssh.dartserver.domain.survey.application.AnswerUpdater;
import com.ssh.dartserver.domain.survey.application.CommentUpdater;
import com.ssh.dartserver.domain.team.application.TeamDeleter;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.PersonalInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserUpdater userUpdater;
    private final UserReader userReader;
    private final FriendDeleter friendDeleter;
    private final TeamDeleter teamDeleter;
    private final CommentUpdater commentUpdater;
    private final AnswerUpdater answerUpdater;
    private final UserDeleter userDeleter;

    public User signUp(User user, PersonalInfo personalInfo, Long universityId) {
        return userUpdater.signUp(user, personalInfo, universityId);
    }

    public User read(Long userId) {
        return userReader.read(userId);
    }

    public User update(User user, String nickname, String profileImageUrl) {
        return userUpdater.updateProfile(user, nickname, profileImageUrl);
    }

    public void delete(User user) {
        friendDeleter.deleteAllFriendShip(user);

        teamDeleter.deleteAllTeamAndRelatedData(user);

        //TODO: 커뮤니티 기능 제거 예정
        answerUpdater.updateAllUserToNullInAnswer(user);
        commentUpdater.updateAllUserToNullInCommentAndRelatedData(user);

        //TODO: soft Delete로 적용 예정
        userDeleter.delete(user);
    }

}
