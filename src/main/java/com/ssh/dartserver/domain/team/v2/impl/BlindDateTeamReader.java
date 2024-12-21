package com.ssh.dartserver.domain.team.v2.impl;

import com.ssh.dartserver.domain.proposal.domain.Proposal;
import com.ssh.dartserver.domain.proposal.infra.ProposalRepository;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.infra.TeamRepository;
import com.ssh.dartserver.domain.team.presentation.response.RegionResponse;
import com.ssh.dartserver.domain.team.v2.dto.BlindDateTeamInfo;
import com.ssh.dartserver.domain.team.v2.dto.BlindDateTeamSearchCondition;
import com.ssh.dartserver.domain.team.v2.dto.BlindDateTeamSimpleInfo;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlindDateTeamReader {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final ProposalRepository proposalRepository;

    // 팀 목록 조회
    @Transactional(readOnly = true)
    public Page<BlindDateTeamSimpleInfo> getTeamList(User user, BlindDateTeamSearchCondition condition) {
        // 검증
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보는 null일 수 없습니다.");
        }
        if (condition == null) {
            throw new IllegalArgumentException("페이징 정보 객체는 null일 수 없습니다.");
        }
        Pageable pageable = PageRequest.of(condition.page(), condition.size());  // sort 구현 x

        // TODO ContextHolder에서 기억하는 User값을 Entity가 아닌 전용 DTO(VO)로 변환해두는 것이 좋아보임. (임시로 사용)
        user = userRepository.findWithUniversityById(user.getId()).orElseThrow();  // 사용자 전체 정보를 조회

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

            images = new ArrayList<>();
            team.getSingleTeamFriends().forEach(teamUser -> images.add(teamUser.getProfileImageUrl().getValue()));
            images.add(user.getPersonalInfo().getProfileImageUrl().getValue());

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

        // 공통 - Proposal 확인하기 (개선 필요)  TODO 테스트 작성 필요
        // TODO 헉!!! 내 팀이랑 상대 팀이랑 일케 비교해야하는데 그렇게 안하고있네... 외부에서 내 팀 번호를 받아오던가 그렇게 해야할듯?
        List<Proposal> proposals = proposalRepository.findAllByRequestingTeamOrRequestedTeam(team, team);  // findAll의 위험성
        boolean isAlreadyProposal = proposals.stream()
                .anyMatch(proposal -> isAlreadyProposal(team, proposal));

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

    /**
     * 특정 팀과 호감을 주고 받았는지 확인한다.
     * @param team 조회하는 사용자가 속한 팀의 id
     * @param proposal 보낸 호감 정보
     * @return 호감을 주거나 받은 경우 true
     */
    private static boolean isAlreadyProposal(Team team, Proposal proposal) {
        Team requested = proposal.getRequestedTeam();  // 요청받은 팀 정보
        Team requesting = proposal.getRequestingTeam();  // 요청한 팀 정보

        boolean exp1 = requested == null ? false : team.getId() == requested.getId();
        boolean exp2 = requesting == null ? false : team.getId() == requesting.getId();

        log.info("이미 호감을 전달했는가?: {}, (requested: {}, requesting: {})", exp1 || exp2, requested!=null ? requested.getId() : null, requesting!=null ? requesting.getId() : null);
        return exp1 || exp2;
    }

}
