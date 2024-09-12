package com.ssh.dartserver.domain.user.application;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.PersonalInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserUpdater userUpdater;
    private final UserReader userReader;
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
        userDeleter.delete(user);
    }

}
