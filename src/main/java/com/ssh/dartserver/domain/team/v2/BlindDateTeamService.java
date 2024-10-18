package com.ssh.dartserver.domain.team.v2;

import com.ssh.dartserver.domain.image.application.ImageUploader;
import com.ssh.dartserver.domain.image.domain.Image;
import com.ssh.dartserver.domain.team.domain.Region;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamImage;
import com.ssh.dartserver.domain.team.domain.TeamRegion;
import com.ssh.dartserver.domain.team.domain.vo.Name;
import com.ssh.dartserver.domain.team.domain.vo.TeamDescription;
import com.ssh.dartserver.domain.team.infra.RegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamRegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamRepository;
import com.ssh.dartserver.domain.team.v2.dto.CreateTeamRequest;
import com.ssh.dartserver.domain.team.v2.dto.UpdateTeamRequest;
import com.ssh.dartserver.domain.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlindDateTeamService {
    private final TeamRepository teamRepository;
    private final TeamRegionRepository teamRegionRepository;
    private final TeamImageRepository teamImageRepository;
    private final RegionRepository regionRepository;
    private final ImageUploader imageUploader;

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

        // 도메인 객체 생성
        BlindDateTeam blindTeam = BlindDateTeam.builder()
                .name(new Name(request.name()))
                .user(user)
                .description(new TeamDescription(request.description()))
                .isVisibleToSameUniversity(request.isVisibleToSameUniversity())
                .regionIds(request.regionIds())
                .imageUrls(request.imageUrls())
                .build();

        // Entity로 매핑 및 저장
        Team team = Team.builder()
                .user(blindTeam.getUser())
                .name(blindTeam.getName().getValue())
                .description(blindTeam.getDescription())
                .isVisibleToSameUniversity(blindTeam.isVisibleToSameUniversity())
                .build();
        teamRepository.save(team);

        // 활동 지역 등록
        List<Region> regions = regionRepository.findAllByIdIn(blindTeam.getRegionIds());
        List<TeamRegion> teamRegions = regions.stream()
                .map(region -> TeamRegion.builder()
                        .region(region)
                        .team(team)
                        .build())
                .toList();
        teamRegions = teamRegionRepository.saveAll(teamRegions);

        // 팀 이미지들 등록
        List<Image> images = imageUploader.saveImageUrls(blindTeam.getImageUrls());
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

    // 팀 수정
    public void updateTeam(User user, UpdateTeamRequest request) {
        throw new UnsupportedOperationException();
    }

    // 팀 삭제
    public void deleteTeam(User user, long teamId) {
        throw new UnsupportedOperationException();
    }

    // 팀 목록 조회
    public void getTeamList(Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    // 팀 상세 조회
    public void getTeamInfo(long teamId) {
        throw new UnsupportedOperationException();
    }

}
