package com.ssh.dartserver.domain.team.v2.impl;

import com.ssh.dartserver.domain.proposal.domain.Proposal;
import com.ssh.dartserver.domain.proposal.infra.ProposalRepository;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.infra.TeamRepository;
import com.ssh.dartserver.domain.team.presentation.response.RegionResponse;
import com.ssh.dartserver.domain.team.v2.dto.BlindDateTeamInfo;
import com.ssh.dartserver.domain.team.v2.dto.BlindDateTeamSimpleInfo;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlindDateTeamReader {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final ProposalRepository proposalRepository;

    // 팀 목록 조회
    @Transactional(readOnly = true)
    public Page<BlindDateTeamSimpleInfo> getTeamList(User user, Pageable pageable) {
        // 검증
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보는 null일 수 없습니다.");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("페이징 정보 객체는 null일 수 없습니다.");
        }

        // TODO ContextHolder에서 기억하는 User값을 Entity가 아닌 전용 DTO(VO)로 변환해두는 것이 좋아보임. (임시로 사용)
        user = userRepository.findWithUniversityById(user.getId()).orElseThrow();

        // TODO 조회수 처리 + 푸시 알림

        Page<Team> teams = teamRepository.findAll(user, pageable);
        List<BlindDateTeamInfo> blindDateTeams = teams.getContent().stream()
                .map(team -> convertBlindDateTeamInfo(team))
                .toList();

        // 결과를 다시 맵핑 (이후 상세조회와 목록조회의 로직이 달라진다면 수정)
        List<BlindDateTeamSimpleInfo> simpleTeams = blindDateTeams.stream()
                .map(team -> BlindDateTeamSimpleInfo.builder()
                        .id(team.id())
                        .leaderId(team.leaderId())
                        .age(team.age())
                        .isCertified(team.isCertified())
                        .universityName(team.universityName())
                        .departmentName(team.departmentName())

                        .name(team.name())
                        .description(team.description())
                        .isVisibleToSameUniversity(team.isVisibleToSameUniversity())

                        .regions(team.regions())
                        .imageUrls(team.imageUrls())
                        .isAlreadyProposalTeam(team.isAlreadyProposalTeam())
                        .teamVersion(team.teamVersion())

                        .build())
                .toList();

        return new PageImpl<>(simpleTeams, pageable, teams.getTotalElements());
    }

    // 내 팀 조회
    // TODO Test 작성 필요
    @Transactional(readOnly = true)
    public BlindDateTeamInfo getUserTeamInfo(User user) {
        Team team = teamRepository.findByLeader_IdOrTeamUsers_User_Id(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 만든 팀이 존재하지 않습니다."));

        return convertBlindDateTeamInfo(team);
    }

    // 팀 상세 조회
    @Transactional(readOnly = true)
    public BlindDateTeamInfo getTeamInfo(long teamId) {
        // TODO 조회수 처리 + 푸시 알림

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀 입니다. teamId: " + teamId));

        return convertBlindDateTeamInfo(team);
    }

    // TODO Test 작성 필요 (클래스 분리 후)
    private BlindDateTeamInfo convertBlindDateTeamInfo(Team team) {
        // v1, v2 분기 처리
        List<String> images;
        long leaderId;
        int age;
        boolean certified;
        String universityName;
        String departmentName;
        String teamDescription;
        String teamVersion;

        if (team.getTeamUsersCombinationHash() != null) {
            // v1 처리
            teamVersion = "v1";
            User user = team.getTeamUsers().get(0).getUser();

            images = List.of(user.getPersonalInfo().getProfileImageUrl().getValue());

            leaderId = user.getId();
            age = user.getPersonalInfo().getBirthYear().getAge();
            certified = user.getStudentVerificationInfo().isCertified();
            universityName = user.getUniversity().getName();
            departmentName = user.getUniversity().getDepartment();
            teamDescription = "";
        } else {
            // v2 처리
            teamVersion = "v2";
            images = team.getTeamImages().stream()
                    .map(teamImage -> teamImage.getImage().getData())
                    .toList();

            leaderId = team.getLeader().getId();
            age = team.getLeader().getPersonalInfo().getBirthYear().getAge();
            certified = team.getLeader().getStudentVerificationInfo().isCertified();
            universityName = team.getLeader().getUniversity().getName();
            departmentName = team.getLeader().getUniversity().getDepartment();
            teamDescription = team.getDescription().getDescription();
        }

        // 공통 - 지역 확인
        List<RegionResponse> regions = team.getTeamRegions().stream()
                .map(teamRegion -> new RegionResponse(teamRegion.getRegion().getId(), teamRegion.getRegion().getName()))
                .toList();

        // 공통 - Proposal 확인하기 (개선 필요)
        List<Proposal> proposals = proposalRepository.findAllByRequestingTeamOrRequestedTeam(team,
                team);  // findAll의 위험성
        boolean isAlreadyProposal = proposals.stream()
                .anyMatch(proposal -> {
                    Team requested = proposal.getRequestedTeam();
                    Team requesting = proposal.getRequestingTeam();

                    boolean exp1 = requested == null ? false : team.getId() == requested.getId();
                    boolean exp2 = requesting == null ? false : team.getId() == requesting.getId();
                    return exp1 || exp2;
                });

        // DTO 생성
        return BlindDateTeamInfo.builder()
                .id(team.getId())
                .leaderId(leaderId)
                .age(age)
                .isCertified(certified)
                .universityName(universityName)
                .departmentName(departmentName)

                .name(team.getName().getValue())
                .description(teamDescription)
                .isVisibleToSameUniversity(team.getIsVisibleToSameUniversity())

                .regions(regions)
                .imageUrls(images)
                .isAlreadyProposalTeam(isAlreadyProposal)
                .teamVersion(teamVersion)

                .build();
    }

}
