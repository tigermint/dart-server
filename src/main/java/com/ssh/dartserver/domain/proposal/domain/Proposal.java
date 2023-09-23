package com.ssh.dartserver.domain.proposal.domain;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.global.common.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Proposal extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proposal_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "proposal_status")
    private ProposalStatus proposalStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requesting_team_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Team requestingTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_team_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Team requestedTeam;

    public void updateProposalStatus(ProposalStatus proposalStatus) {
        this.proposalStatus = proposalStatus;
    }

    public void updateProposalOnTeamDeletion(Long teamId) {
        if(this.requestingTeam.getId().equals(teamId)) {
            this.requestingTeam = null;
        }
        if(this.requestedTeam.getId().equals(teamId)) {
            this.requestedTeam = null;
        }
    }
}
