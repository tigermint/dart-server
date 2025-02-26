package com.ssh.dartserver.domain.team.v2.impl;

import com.ssh.dartserver.domain.image.domain.Image;
import com.ssh.dartserver.domain.image.domain.ImageType;
import com.ssh.dartserver.domain.team.domain.Region;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamImage;
import com.ssh.dartserver.domain.team.domain.TeamRegion;
import com.ssh.dartserver.domain.team.infra.RegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamRegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamRepository;
import com.ssh.dartserver.domain.team.v2.TeamImageRepository;
import com.ssh.dartserver.domain.team.v2.dto.UpdateTeamRequest;
import com.ssh.dartserver.domain.user.domain.User;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlindDateTeamUpdater {

    private final TeamRepository teamRepository;
    private final TeamRegionRepository teamRegionRepository;
    private final TeamImageRepository teamImageRepository;
    private final RegionRepository regionRepository;

    // 팀 수정 (Put)
    @Transactional
    public void updateTeam(User user, long teamId, UpdateTeamRequest request) {
        log.info("팀 정보 수정 요청이 들어왔습니다. TeamId: {}, Request: {}", teamId, request);

        // 검증
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보는 null일 수 없습니다.");
        }
        if (request == null) {
            throw new IllegalArgumentException("팀 수정 요청(UpdateTeamRequest)는 null일 수 없습니다.");
        }

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀 입니다. teamId: " + teamId));

        if (team.getTeamUsersCombinationHash() != null) {
            throw new IllegalStateException("v1 버전에서 만든 팀은 수정할 수 없습니다. 팀을 삭제하고 다시 생성해주세요.");
        }

        if (!team.isLeader(user)) {
            throw new IllegalArgumentException("자신이 만든 팀만 삭제할 수 있습니다. team.leaderId: " + team.getLeader().getId());
        }

        // regionId로 regions 가져오기
        teamRegionRepository.deleteAllByTeamId(team.getId());

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
        teamImages.stream()
                .filter(teamImage -> !request.imageUrls().contains(teamImage.getImage().getData()))
                .forEach(teamImageRepository::delete);

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

}
