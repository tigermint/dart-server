package com.ssh.dartserver.domain.team.domain;

import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.global.common.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Team extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Embedded
    private Name name;

    @Embedded
    private TeamUsersCombinationHash teamUsersCombinationHash;

    @Column(name = "is_visible_to_same_university")
    private Boolean isVisibleToSameUniversity;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private University university;

    @Builder
    public Team(String name, Boolean isVisibleToSameUniversity, University university, TeamUsersCombinationHash teamUsersCombinationHash) {
        this.name = Name.from(name);
        this.isVisibleToSameUniversity = isVisibleToSameUniversity;
        this.university = university;
        this.teamUsersCombinationHash = teamUsersCombinationHash;
    }

    public void update(String name, Boolean isVisibleToSameUniversity, TeamUsersCombinationHash teamUsersCombinationHash) {
        this.name = Name.from(name);
        this.isVisibleToSameUniversity = isVisibleToSameUniversity;
        this.teamUsersCombinationHash = teamUsersCombinationHash;
    }
}
