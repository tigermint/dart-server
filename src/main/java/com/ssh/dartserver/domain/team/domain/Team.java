package com.ssh.dartserver.domain.team.domain;

import com.ssh.dartserver.domain.proposal.domain.Proposal;
import com.ssh.dartserver.domain.team.domain.vo.Name;
import com.ssh.dartserver.domain.team.domain.vo.TeamDescription;
import com.ssh.dartserver.domain.team.domain.vo.TeamUsersCombinationHash;
import com.ssh.dartserver.domain.team.domain.vo.ViewCount;
import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.global.common.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Team extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private User leader;

    @Embedded
    private Name name;

    @Embedded
    private TeamDescription description;

    @Column(name = "is_visible_to_same_university")
    private Boolean isVisibleToSameUniversity;

    @Embedded
    private ViewCount viewCount;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<TeamRegion> teamRegions = new ArrayList<>();

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<TeamImage> teamImages = new ArrayList<>();

    // TODO - 이 주석을 제거하세요. Deprecated 20241015
    @Deprecated(since = "20241015", forRemoval = false)
    @Embedded
    private TeamUsersCombinationHash teamUsersCombinationHash;

    @Deprecated(since = "20241015", forRemoval = false)
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<TeamUser> teamUsers = new ArrayList<>();

    @Deprecated(since = "20241015", forRemoval = false)
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    @BatchSize(size = 500)
    private List<SingleTeamFriend> singleTeamFriends = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "university_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private University university;

    // TODO 이걸 꼭 팀에서 알아야하나 확인 2024-10-15 @HyeonSik Choi
    @OneToMany(mappedBy = "requestedTeam")
    private List<Proposal> requestedTeamProposals = new ArrayList<>();

    @Builder
    public Team(String name, User user, TeamDescription description, Boolean isVisibleToSameUniversity, University university, TeamUsersCombinationHash teamUsersCombinationHash) {
        this.name = Name.from(name);
        this.leader = user;
        this.description = description;
        this.isVisibleToSameUniversity = isVisibleToSameUniversity;
        this.university = university;
        this.teamUsersCombinationHash = teamUsersCombinationHash;
        this.viewCount = ViewCount.from(0);
    }

    @Deprecated(since = "20241028")
    // v1 team update
    public void update(String name, Boolean isVisibleToSameUniversity, TeamUsersCombinationHash teamUsersCombinationHash) {
        this.name = Name.from(name);
        this.isVisibleToSameUniversity = isVisibleToSameUniversity;
        this.teamUsersCombinationHash = teamUsersCombinationHash;
    }

    // v2 team update
    public void update(String name, String description, boolean isVisibleToSameUniversity, List<TeamRegion> teamRegions, List<TeamImage> teamImages) {
        this.name = Name.from(name);
        this.description = new TeamDescription(description);
        this.isVisibleToSameUniversity = isVisibleToSameUniversity;
        setTeamRegions(teamRegions);
        setTeamImages(teamImages);
    }

    public void increaseViewCount(int viewCountIncrement) {
        this.viewCount = this.viewCount.increase(viewCountIncrement);
    }

    public boolean isLeader(User user) {
        return leader.equals(user);
    }

    public void setTeamRegions(List<TeamRegion> teamRegions) {
        this.teamRegions.clear();
        this.teamRegions.addAll(teamRegions);
    }

    public void setTeamImages(List<TeamImage> teamImages) {
        this.teamImages.clear();
        this.teamImages.addAll(teamImages);
    }

}
