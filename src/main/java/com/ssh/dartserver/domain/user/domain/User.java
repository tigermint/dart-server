package com.ssh.dartserver.domain.user.domain;

import com.ssh.dartserver.domain.user.domain.personalinfo.PersonalInfo;
import com.ssh.dartserver.domain.user.domain.recommendcode.RandomRecommendCodeGeneratable;
import com.ssh.dartserver.global.common.BaseTimeEntity;
import com.ssh.dartserver.global.common.Role;
import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.user.domain.recommendcode.RecommendationCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {
    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String password;

    @Embedded
    private PersonalInfo personalInfo;

    @Embedded
    private NextVoteAvailableDateTime nextVoteAvailableDateTime;

    @Enumerated(EnumType.STRING)
    private Role role;
    @Embedded
    private RecommendationCode recommendationCode;

    @Column(unique = true)
    private String username;
    private String providerId;
    private String provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private University university;

    public void updateWithRecommendationCode(PersonalInfo personalInfo, University university, RandomRecommendCodeGeneratable randomGenerator) {
        this.personalInfo = personalInfo;
        this.university = university;
        this.nextVoteAvailableDateTime = NextVoteAvailableDateTime.newInstance();
        this.recommendationCode = RecommendationCode.generate(randomGenerator);
    }

    public void update(PersonalInfo personalInfo, University university) {
        this.personalInfo = personalInfo;
        this.university = university;
    }

    public void updateNextVoteAvailableDateTime(int value) {
        this.nextVoteAvailableDateTime = NextVoteAvailableDateTime.plusMinutes(value);
    }

}
