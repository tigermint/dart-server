package com.ssh.dartserver.domain.user;

import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.user.domain.AuthInfo;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.domain.user.domain.personalinfo.PersonalInfo;

public class UserTestFixture {

    public static PersonalInfo getPersonalInfo() {
        return PersonalInfo.of(
                "홍길동",
                "01012345678",
                Gender.MALE,
                2021,
                1998
        );
    }

    public static User getLoggedInUser(Long userId) {
        return User.builder()
                .id(userId)
                .authInfo(AuthInfo.of("kakao_" + userId, String.valueOf(userId), "kakao"))
                .build();
    }

    public static User getSignedUpUser(Long userId, University university) {
        final User loggedInUser = getLoggedInUser(userId);
        return loggedInUser.signUp(getPersonalInfo(), university);
    }
}
