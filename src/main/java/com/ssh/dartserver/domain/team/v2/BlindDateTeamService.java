package com.ssh.dartserver.domain.team.v2;

import com.ssh.dartserver.domain.image.application.ImageUploader;
import com.ssh.dartserver.domain.image.domain.Image;
import com.ssh.dartserver.domain.image.domain.ImageType;
import com.ssh.dartserver.domain.proposal.domain.Proposal;
import com.ssh.dartserver.domain.proposal.infra.ProposalRepository;
import com.ssh.dartserver.domain.team.domain.Region;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamImage;
import com.ssh.dartserver.domain.team.domain.TeamRegion;
import com.ssh.dartserver.domain.team.domain.vo.Name;
import com.ssh.dartserver.domain.team.domain.vo.TeamDescription;
import com.ssh.dartserver.domain.team.infra.RegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamRegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamRepository;
import com.ssh.dartserver.domain.team.presentation.response.RegionResponse;
import com.ssh.dartserver.domain.team.v2.dto.BlindDateTeamInfo;
import com.ssh.dartserver.domain.team.v2.dto.BlindDateTeamSimpleInfo;
import com.ssh.dartserver.domain.team.v2.dto.CreateTeamRequest;
import com.ssh.dartserver.domain.team.v2.dto.UpdateTeamRequest;
import com.ssh.dartserver.domain.user.domain.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlindDateTeamService {
    private final TeamRepository teamRepository;
    private final TeamRegionRepository teamRegionRepository;
    private final TeamImageRepository teamImageRepository;
    private final RegionRepository regionRepository;
    private final ImageUploader imageUploader;
    private final ProposalRepository proposalRepository;

    // 팀 생성
    @Transactional
    public void createTeam(User user, CreateTeamRequest request) {
        // 검증
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보는 null일 수 없습니다.");
        }
        if (request == null) {
            throw new IllegalArgumentException("팀 생성 요청(CreateTeamRequest)는 null일 수 없습니다.");
        }
        if (teamRepository.existsByLeader_Id(user.getId())) {
            throw new IllegalStateException("사용자는 하나의 팀만 생성할 수 있습니다. 이미 생성한 팀이 존재합니다. userId=" + user.getId());
        }

        // Entity로 매핑 및 저장
        Team team = Team.builder()
                .user(user)
                .name(request.name())
                .description(new TeamDescription(request.description()))
                .isVisibleToSameUniversity(request.isVisibleToSameUniversity())
                .build();
        teamRepository.save(team);

        // 활동 지역 등록
        List<Region> regions = regionRepository.findAllByIdIn(request.regionIds());
        List<TeamRegion> teamRegions = regions.stream()
                .map(region -> TeamRegion.builder()
                        .region(region)
                        .team(team)
                        .build())
                .toList();
        teamRegions = teamRegionRepository.saveAll(teamRegions);

        // 팀 이미지들 등록
        List<Image> images = imageUploader.saveImageUrls(request.imageUrls());
        List<TeamImage> teamImages = images.stream()
                .map(image -> TeamImage.builder()
                        .team(team)
                        .image(image)
                        .build())
                .toList();
        teamImages = teamImageRepository.saveAll(teamImages);

        team.setTeamRegions(teamRegions);
        team.setTeamImages(teamImages);
        teamRepository.save(team);
    }

    // 팀 수정 (Put)
    @Transactional
    public void updateTeam(User user, UpdateTeamRequest request) {
        // 검증
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보는 null일 수 없습니다.");
        }
        if (request == null) {
            throw new IllegalArgumentException("팀 수정 요청(UpdateTeamRequest)는 null일 수 없습니다.");
        }

        Team team = teamRepository.findById(request.teamId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀 입니다. teamId: " + request.teamId()));

        if (!team.isLeader(user)) {
            throw new IllegalArgumentException("자신이 만든 팀만 삭제할 수 있습니다. team.leaderId: " + team.getLeader().getId());
        }

        // regionId로 regions 가져오기
        List<Region> regions = request.regionIds().stream()
                .map(regionId -> regionRepository.findById(regionId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 RegionId입니다. value: " + regionId)))
                .toList();

        List<TeamRegion> teamRegions = teamRegionRepository.findAllByTeamId(team.getId());
        List<TeamRegion> teamRegions1 = regions.stream()
                .map(region -> {
                    Optional<TeamRegion> teamRegion = findTeamRegionByRegion(teamRegions, region);
                    return teamRegion.orElseGet(() -> new TeamRegion(team, region));
                })
                .toList();

        // ImageUrl을 비교하여 아직 등록되지 않은 이미지만 새로 등록
        List<TeamImage> teamImages = teamImageRepository.findAllByTeam(team);
        List<TeamImage> teamImages1 = request.imageUrls().stream()
                .map(imageUrl -> {
                    Optional<TeamImage> teamImage = findTeamImageByImageUrl(teamImages, imageUrl);
                    return teamImage.orElseGet(() -> {
                        Image image = new Image(ImageType.URL, imageUrl);
                        return new TeamImage(team, image);
                    });
                })
                .toList();

        // 로직
        team.setName(Name.from(request.name()));
        team.setDescription(new TeamDescription(request.description()));
        team.setIsVisibleToSameUniversity(request.isVisibleToSameUniversity());
        team.setTeamRegions(teamRegions1);
        team.setTeamImages(teamImages1);

        teamRepository.save(team);
    }

    private Optional<TeamRegion> findTeamRegionByRegion(List<TeamRegion> teamRegions, Region region) {
        return teamRegions.stream()
                .filter(teamRegion -> teamRegion.isRegionEqual(region))
                .findAny();
    }

    private Optional<TeamImage> findTeamImageByImageUrl(List<TeamImage> teamImages, String imageUrl) {
        return teamImages.stream()
                .filter(teamImage -> teamImage.getImage().isImageUrlEqual(imageUrl))
                .findAny();
    }

    // 팀 삭제
    @Transactional
    public void deleteTeam(User user, long teamId) {
        // 검증
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보는 null일 수 없습니다.");
        }

        Optional<Team> team = teamRepository.findById(teamId);
        if (team.isEmpty()) {
            return;  // 이미 삭제된 경우, 무시
        }

        // TODO v1은 leader가 존재하지 않으므로 기존 삭제 로직을 사용할 것
        if (!team.get().isLeader(user)) {
            throw new IllegalArgumentException("자신이 만든 팀만 삭제할 수 있습니다. team.leaderId: " + team.get().getLeader().getId());
        }

        // 로직
        teamRepository.delete(team.get());  // 연관 객체 처리는 어케되있는지 확인!
    }

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

        Page<Team> teams = teamRepository.findAll(user, pageable);
        List<BlindDateTeamSimpleInfo> blindDateTeams = teams.getContent().stream()
                .map(team -> {
                    // TODO 조회수 처리 + 푸시 알림
                    // TODO v1, v2 분기 처리

                    List<RegionResponse> regions = team.getTeamRegions().stream()
                            .map(teamRegion -> new RegionResponse(teamRegion.getRegion().getId(),
                                    teamRegion.getRegion().getName()))
                            .toList();

                    List<String> images = team.getTeamImages().stream()
                            .map(teamImage -> teamImage.getImage().getData())
                            .toList();

                    // Proposal 확인하기 (개선 필요)
                    List<Proposal> proposals = proposalRepository.findAllByRequestingTeamOrRequestedTeam(team,
                            team);  // findAll의 위험성
                    boolean isAlreadyProposal = proposals.stream()
                            .anyMatch(proposal -> team.getId() == proposal.getRequestedTeam().getId()
                                    || team.getId() == proposal.getRequestingTeam().getId());

                    // v2 처리
                    return BlindDateTeamSimpleInfo.builder()
                            .id(team.getId())
                            .leaderId(team.getLeader().getId())
                            .age(team.getLeader().getPersonalInfo().getBirthYear().getAge())
                            .isCertified(team.getLeader().getStudentVerificationInfo().isCertified())
                            .universityName(team.getLeader().getUniversity().getName())
                            .departmentName(team.getLeader().getUniversity().getDepartment())

                            .name(team.getName().getValue())
                            .description(team.getDescription().getDescription())
                            .isVisibleToSameUniversity(team.getIsVisibleToSameUniversity())

                            .regions(regions)
                            .imageUrls(images)
                            .isAlreadyProposalTeam(isAlreadyProposal)

                            .build();
                })
                .toList();

        return new PageImpl<>(blindDateTeams, pageable, teams.getTotalElements());
    }

    // 팀 상세 조회
    @Transactional(readOnly = true)
    public BlindDateTeamInfo getTeamInfo(long teamId) {
        // TODO 조회수 처리 + 푸시 알림
        // TODO v1, v2 분기 처리
        // if (team.getTeamUsersCombinationHash() != null) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀 입니다. teamId: " + teamId));

        List<RegionResponse> regions = team.getTeamRegions().stream()
                .map(teamRegion -> new RegionResponse(teamRegion.getRegion().getId(), teamRegion.getRegion().getName()))
                .toList();

        List<String> images = team.getTeamImages().stream()
                .map(teamImage -> teamImage.getImage().getData())
                .toList();

        // Proposal 확인하기 (개선 필요)
        List<Proposal> proposals = proposalRepository.findAllByRequestingTeamOrRequestedTeam(team, team);  // findAll의 위험성
        boolean isAlreadyProposal = proposals.stream()
                .anyMatch(proposal -> team.getId() == proposal.getRequestedTeam().getId() || team.getId() == proposal.getRequestingTeam().getId());

        // v2 처리
        return BlindDateTeamInfo.builder()
                .id(team.getId())
                .leaderId(team.getLeader().getId())
                .age(team.getLeader().getPersonalInfo().getBirthYear().getAge())
                .isCertified(team.getLeader().getStudentVerificationInfo().isCertified())
                .universityName(team.getLeader().getUniversity().getName())
                .departmentName(team.getLeader().getUniversity().getDepartment())

                .name(team.getName().getValue())
                .description(team.getDescription().getDescription())
                .isVisibleToSameUniversity(team.getIsVisibleToSameUniversity())

                .regions(regions)
                .imageUrls(images)
                .isAlreadyProposalTeam(isAlreadyProposal)

                .build();
    }

}
