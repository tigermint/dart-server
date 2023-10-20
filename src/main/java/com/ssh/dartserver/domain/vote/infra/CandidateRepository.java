package com.ssh.dartserver.domain.vote.infra;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.vote.domain.Candidate;
import com.ssh.dartserver.domain.vote.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    @Modifying
    @Query("update Candidate c set c.user = null where c.user = :user")
    void updateAllUserToNull(@Param("user") User user);
    @Modifying
    @Query("delete from Candidate c where c.vote in :pickedUserVotes")
    void deleteAllByVoteIn(@Param("pickedUserVotes") List<Vote> pickedUserVotes);
}
