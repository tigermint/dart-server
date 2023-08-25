package com.ssh.dartserver.domain.vote.infra;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.vote.domain.Candidate;
import com.ssh.dartserver.domain.vote.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findAllByUser(User user);

    void deleteAllByVoteIn(List<Vote> pickedUserVotes);
}
