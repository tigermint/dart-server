package com.ssh.dartserver.domain.user.service;

import com.ssh.dartserver.domain.university.service.UniversityReader;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.PersonalInfo;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserUpdater {

    private final UniversityReader universityReader;

    private final UserRepository userRepository;

    @Transactional
    public User signUp(User user, PersonalInfo personalInfo, Long universityId) {
        user.signUp(personalInfo, universityReader.read(universityId));
        return userRepository.save(user);
    }

    @Transactional
    public User updateProfile(User user, String nickname, String profileImageUrl) {
        user.updateNickname(nickname);
        user.updateProfileImageUrl(profileImageUrl);
        return userRepository.save(user);
    }
}
