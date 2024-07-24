package com.ssh.dartserver.domain.user.domain;

import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.user.domain.personalinfo.BirthYear;
import com.ssh.dartserver.domain.user.domain.personalinfo.Nickname;
import com.ssh.dartserver.domain.user.domain.personalinfo.PersonalInfo;
import com.ssh.dartserver.domain.user.domain.personalinfo.ProfileImageUrl;
import com.ssh.dartserver.domain.user.domain.profilequestions.ProfileQuestions;
import com.ssh.dartserver.domain.user.domain.studentverificationinfo.StudentVerificationInfo;
import com.ssh.dartserver.global.common.BaseTimeEntity;
import com.ssh.dartserver.global.common.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Embedded
    private AuthInfo authInfo;

    @Embedded
    private PersonalInfo personalInfo;

    @Embedded
    private StudentVerificationInfo studentVerificationInfo;

    @Embedded
    private RecommendationCode recommendationCode;

    @Embedded
    private Point point;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Deprecated(since = "20240703", forRemoval = true)
    @Embedded
    private ProfileQuestions profileQuestions;

    @Deprecated(since = "20240703", forRemoval = true)
    @Embedded
    private NextVoteAvailableDateTime nextVoteAvailableDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private University university;

    @Builder
    private User(
            final Long id,
            final AuthInfo authInfo,
            final PersonalInfo personalInfo,
            final StudentVerificationInfo studentVerificationInfo,
            final RecommendationCode recommendationCode,
            final Point point,
            final Role role,
            final ProfileQuestions profileQuestions,
            final NextVoteAvailableDateTime nextVoteAvailableDateTime,
            final University university) {
        this.id = id;
        this.authInfo = authInfo;
        this.personalInfo = personalInfo;
        this.studentVerificationInfo = studentVerificationInfo;
        this.recommendationCode = recommendationCode;
        this.point = point;
        this.role = role;
        this.profileQuestions = profileQuestions;
        this.nextVoteAvailableDateTime = nextVoteAvailableDateTime;
        this.university = university;
    }

    public static User of(
            final String username,
            final String providerId,
            final String provider
            ) {
        return User.builder()
                .authInfo(AuthInfo.of(username, providerId, provider))
                .build();
    }

    public User signUp(
            final PersonalInfo personalInfo,
            final University university
    ) {
        this.personalInfo = personalInfo;
        this.studentVerificationInfo = StudentVerificationInfo.newInstance();
        this.recommendationCode = RecommendationCode.newInstance();
        this.point = Point.newInstance();
        this.role = Role.USER;
        this.university = university;
        return this;
    }

    public static User createSingleTeamFriendUser(
            String nickname,
            int birthYear,
            String profileImageUrl,
            University university) {
        return User.builder()
                .personalInfo(PersonalInfo.builder()
                        .nickname(Nickname.from(nickname))
                        .birthYear(BirthYear.from(birthYear))
                        .profileImageUrl(ProfileImageUrl.from(profileImageUrl))
                        .build())
                .studentVerificationInfo(StudentVerificationInfo.newInstance())
                .university(university)
                .build();
    }

    public void updateNickname(String value) {
        this.personalInfo.updateNickname(value);
    }

    public void updateProfileImageUrl(String value) {
        this.personalInfo.updateProfileImageUrl(value);
    }

    public void updateNextVoteAvailableDateTime(int value) {
        this.nextVoteAvailableDateTime = NextVoteAvailableDateTime.plusMinutes(value);
    }

    public void addPoint(int value) {
        this.point = this.point.add(value);
    }

    public void subtractPoint(int value) {
        this.point = this.point.subtract(value);
    }

    public String getNicknameOrElseName() {
        if (!Objects.equals(this.getPersonalInfo().getNickname().getValue(), "DEFAULT")) {
            return this.getPersonalInfo().getNickname().getValue();
        }
        return this.getPersonalInfo().getName().getValue();
    }
}
