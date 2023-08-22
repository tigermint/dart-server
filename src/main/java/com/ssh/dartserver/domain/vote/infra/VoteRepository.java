package com.ssh.dartserver.domain.vote.infra;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.vote.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByPickedUserAndId(User pickedUser, Long voteId);
    List<Vote> findAllByPickingUserId(Long id);
    List<Vote> findAllByPickedUserId(Long id);

    List<Vote> findAllByPickedUser(User pickedUser);

    void deleteAllByPickedUserId(Long id);

}