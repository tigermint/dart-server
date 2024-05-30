package com.ssh.dartserver.domain.proposal;

import static com.ssh.dartserver.domain.proposal.ProposalSteps.가입하지않은팀원_생성;
import static com.ssh.dartserver.domain.proposal.ProposalSteps.팀생성요청_생성;
import static com.ssh.dartserver.domain.region.RegionSteps.지역생성요청_생성;
import static com.ssh.dartserver.domain.university.UniversitySteps.대학생성요청_생성;

import com.ssh.dartserver.domain.team.dto.TeamRequest;
import com.ssh.dartserver.domain.team.dto.TeamRequest.SingleTeamFriendDto;
import com.ssh.dartserver.domain.team.infra.RegionRepository;
import com.ssh.dartserver.domain.team.service.MyTeamService;
import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProposalTestHelper {
    @Autowired
    private MyTeamService myTeamService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private UniversityRepository universityRepository;

    public void 팀만들기(long userId) {
        // Team 정보 생성
        List<Long> regionIds = new ArrayList<>();
        regionIds.add(1L);
        List<Long> userIds = new ArrayList<>();
        List<SingleTeamFriendDto> friends = new ArrayList<>();
        friends.add(가입하지않은팀원_생성());

        // Team Request 생성
        final TeamRequest request = 팀생성요청_생성(regionIds, userIds, friends);

        // User Entity 가져오기
        final User user = userRepository.findById(userId).get();

        // 새 팀 등록하기
        myTeamService.createSingleTeam(user, request);
    }

    public void 대학생성(int count) {
        final List<University> universities = 대학생성요청_생성(count);
        universityRepository.saveAll(universities);
    }

    public void 지역생성(int count) {
        regionRepository.saveAll(지역생성요청_생성(count));
    }
}
