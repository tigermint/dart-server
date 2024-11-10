package com.ssh.dartserver.domain.team.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "team_region")
public class TeamRegion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_region_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Region region;

    public boolean isRegionEqual(Region region) {
        return this.region == region;
    }

    public TeamRegion(Team team, Region region) {
        this.team = team;
        this.region = region;
    }

    @Override
    public String toString() {
        return "TeamRegion{" +
                "id=" + id +
                ", teamId=" + team.getId() +
                ", region=" + region.toString() +
                '}';
    }
}
