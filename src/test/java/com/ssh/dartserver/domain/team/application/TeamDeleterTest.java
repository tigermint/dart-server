package com.ssh.dartserver.domain.team.application;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.UserManager;
import com.ssh.dartserver.domain.proposal.ProposalTestHelper;
import com.ssh.dartserver.domain.region.RegionSteps;
import com.ssh.dartserver.domain.team.infra.RegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamRepository;
import com.ssh.dartserver.domain.university.UniversitySteps;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.testing.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class TeamDeleterTest extends ApiTest {

    @Autowired
    private TeamDeleter teamDeleter;

    @Autowired
    private ProposalTestHelper proposalTestHelper;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserManager userManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UniversityRepository universityRepository;

    @Autowired
    private RegionRepository regionRepository;

    @BeforeEach
    void setUp() {
        universityRepository.saveAll(UniversitySteps.대학생성요청_생성(3));
        regionRepository.saveAll(RegionSteps.지역생성요청_생성(3));
        userManager.createTestUserWithInformation();
    }

    @Nested
    class deleteAllTeamAndRelatedData {

        @Nested
        @DisplayName("User가 존재할 때")
        class user_is_valid {

            @Test
            @DisplayName("소속된 팀이 없다면, 성공적으로 수행된다.")
            void user_is_valid_but_have_not_team_success() {
                // given
                User user = userRepository.findAll().get(0);

                // expect
                assertThatNoException().isThrownBy(
                        () -> teamDeleter.deleteAllTeamAndRelatedData(user)
                );
            }

            @Test
            @DisplayName("소속된 팀이 있다면, 성공적으로 제거한다.")
            void user_is_valid_and_have_team_success() {
                // given
                User user = userRepository.findAll().get(0);  // Assuming a user is already saved in the DB
                proposalTestHelper.팀만들기(user.getId());

                // when
                teamDeleter.deleteAllTeamAndRelatedData(user);

                // then
                long teamCount = teamRepository.count();
                assertThat(teamCount).isZero();
            }

        }

        @Nested
        @DisplayName("User가 존재하지 않을 때")
        class user_is_null {

            @Test
            @DisplayName("성공적으로 동작이 수행된다.")
            void user_is_null_success() {
                // expect
                assertThatNoException().isThrownBy(
                        () -> teamDeleter.deleteAllTeamAndRelatedData(null)
                );
            }

        }

    }

}