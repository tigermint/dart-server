package com.ssh.dartserver.domain.proposal.infra;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.testing.IntegrationTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class ProposalRepositoryTest extends ApiTest {

    @Autowired
    private ProposalRepository proposalRepository;

    @Nested
    class updateRequestingOrRequestedTeamsToNullIn {

        @Test
        @DisplayName("빈 List가 주어지면, 정상적으로 처리한다.")
        void empty_list_success() {
            // given
            List<Team> teams = List.of();

            // expect
            assertThatNoException().isThrownBy(
                    () -> proposalRepository.updateRequestingOrRequestedTeamsToNullIn(teams)
            );
        }

    }

}
