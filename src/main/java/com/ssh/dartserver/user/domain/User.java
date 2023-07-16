package com.ssh.dartserver.user.domain;

import com.ssh.dartserver.common.domain.BaseTimeEntity;
import com.ssh.dartserver.common.domain.Role;
import com.ssh.dartserver.university.domain.University;
import com.ssh.dartserver.user.domain.personalinfo.PersonalInfo;
import com.ssh.dartserver.user.domain.recommendcode.RandomRecommendCodeGeneratable;
import com.ssh.dartserver.user.domain.recommendcode.RecommendationCode;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

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
        this.nextVoteAvailableDateTime = new NextVoteAvailableDateTime(LocalDateTime.now());
        this.recommendationCode = RecommendationCode.generate(randomGenerator);
    }

    public void update(PersonalInfo personalInfo, University university) {
        this.personalInfo = personalInfo;
        this.university = university;
    }

}
