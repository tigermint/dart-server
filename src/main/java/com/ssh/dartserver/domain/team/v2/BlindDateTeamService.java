package com.ssh.dartserver.domain.team.v2;

import com.ssh.dartserver.domain.image.application.ImageUploader;
import com.ssh.dartserver.domain.image.domain.Image;
import com.ssh.dartserver.domain.image.domain.ImageType;
import com.ssh.dartserver.domain.proposal.domain.Proposal;
import com.ssh.dartserver.domain.proposal.infra.ProposalRepository;
import com.ssh.dartserver.domain.team.application.MyTeamService;
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
import com.ssh.dartserver.domain.user.infra.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlindDateTeamService {
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamRegionRepository teamRegionRepository;
    private final TeamImageRepository teamImageRepository;
    private final RegionRepository regionRepository;
    private final ImageUploader imageUploader;
    private final ProposalRepository proposalRepository;
    private final MyTeamService myTeamService;

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

        // TODO v1형태 (combinationHash 또는 teamUser)로도 중복 팀 생성 검사 필요
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

        if (team.getTeamUsersCombinationHash() != null) {
            throw new IllegalStateException("v1 버전에서 만든 팀은 수정할 수 없습니다. 팀을 삭제하고 다시 생성해주세요.");
        }

        if (!team.isLeader(user)) {
            throw new IllegalArgumentException("자신이 만든 팀만 삭제할 수 있습니다. team.leaderId: " + team.getLeader().getId());
        }

        // TODO 더 이상 사용되지 않는 regions, teamImage를 명시적으로 삭제해야 함
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

        log.info("NewRegions: {}", teamRegions1);

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
        team.update(request.name(), request.description(), request.isVisibleToSameUniversity(), teamRegions1, teamImages1);
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

        if (team.get().getTeamUsersCombinationHash() != null) {
            // v1 처리
            log.debug("v1 팀을 삭제합니다.");
            myTeamService.deleteTeam(user, teamId);
            return;
        }

        // v2 처리
        log.debug("v2 팀을 삭제합니다.");
        if (!team.get().isLeader(user)) {
            throw new IllegalArgumentException(
                    "자신이 만든 팀만 삭제할 수 있습니다. team.leaderId: " + team.get().getLeader().getId());
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

        // TODO ContextHolder에서 기억하는 User값을 Entity가 아닌 전용 DTO(VO)로 변환해두는 것이 좋아보임. (임시로 사용)
        user = userRepository.findWithUniversityById(user.getId()).orElseThrow();
        System.out.println("대학교이름! " + user.getUniversity().getName());

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

    private BlindDateTeamInfo convertBlindDateTeamInfo(Team team) {
        // v1, v2 분기 처리
        List<String> images;
        long leaderId;
        int age;
        boolean certified;
        String universityName;
        String departmentName;
        String teamDescription;

        if (team.getTeamUsersCombinationHash() != null) {
            // v1 처리
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

        // v2 처리
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

                .build();
    }

}
