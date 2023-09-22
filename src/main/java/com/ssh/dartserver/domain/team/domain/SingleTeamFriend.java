package com.ssh.dartserver.domain.team.domain;

import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.user.domain.personalinfo.BirthYear;
import com.ssh.dartserver.domain.user.domain.personalinfo.Nickname;
import com.ssh.dartserver.domain.user.domain.personalinfo.ProfileImageUrl;
import lombok.*;

import javax.persistence.*;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SingleTeamFriend {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "single_team_friend_profile_id")
    private Long id;

    @Embedded
    private Nickname nickname;

    @Embedded
    private BirthYear birthYear;

    @Column(name = "profile_image_url")
    private ProfileImageUrl profileImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private University university;

    @Builder
    public SingleTeamFriend(String nickname, int birthYear, String profileImageUrl, Team team, University university) {
        this.nickname = Nickname.from(nickname);
        this.birthYear = BirthYear.from(birthYear);
        this.profileImageUrl = Optional.ofNullable(profileImageUrl)
                .map(ProfileImageUrl::from)
                .orElse(ProfileImageUrl.from("DEFAULT"));
        this.team = team;
        this.university = university;
    }
}
