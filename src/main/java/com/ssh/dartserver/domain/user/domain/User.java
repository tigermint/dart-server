package com.ssh.dartserver.domain.user.domain;

import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.user.domain.personalinfo.BirthYear;
import com.ssh.dartserver.domain.user.domain.personalinfo.Nickname;
import com.ssh.dartserver.domain.user.domain.personalinfo.PersonalInfo;
import com.ssh.dartserver.domain.user.domain.personalinfo.ProfileImageUrl;
import com.ssh.dartserver.domain.user.domain.profilequestions.ProfileQuestions;
import com.ssh.dartserver.domain.user.domain.recommendcode.RandomRecommendCodeGeneratable;
import com.ssh.dartserver.domain.user.domain.recommendcode.RecommendationCode;
import com.ssh.dartserver.domain.user.domain.studentverificationinfo.StudentVerificationInfo;
import com.ssh.dartserver.global.common.BaseTimeEntity;
import com.ssh.dartserver.global.common.Role;

import java.util.Objects;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String password;

    @Embedded
    private PersonalInfo personalInfo;

    @Embedded
    private StudentVerificationInfo studentVerificationInfo;

    @Embedded
    private NextVoteAvailableDateTime nextVoteAvailableDateTime;

    @Embedded
    private Point point;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Embedded
    private RecommendationCode recommendationCode;

    @Embedded
    private ProfileQuestions profileQuestions;

    @Column(unique = true)
    private String username;
    private String providerId;
    private String provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private University university;

    public void signup(PersonalInfo personalInfo, University university, RandomRecommendCodeGeneratable randomGenerator, Point point) {
        this.personalInfo = personalInfo;
        this.university = university;
        this.studentVerificationInfo = StudentVerificationInfo.newInstance();
        this.nextVoteAvailableDateTime = NextVoteAvailableDateTime.newInstance();
        this.recommendationCode = RecommendationCode.generate(randomGenerator);
        this.point = point;
        this.profileQuestions = ProfileQuestions.newInstance();
    }

    public static User createSingleTeamFriendUser(String nickname, int birthYear, String profileImageUrl, University university) {
        return User.builder()
                .personalInfo(PersonalInfo.builder()
                        .nickname(Nickname.from(nickname))
                        .birthYear(BirthYear.from(birthYear))
                        .profileImageUrl(ProfileImageUrl.from(profileImageUrl))
                        .build())
                .university(university)
                .studentVerificationInfo(StudentVerificationInfo.newInstance())
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
