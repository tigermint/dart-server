package com.ssh.dartserver.domain.vote.infra;

import com.ssh.dartserver.domain.vote.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findAllByPickedUserId(Long id);

    void deleteAllByPickedUserId(Long id);

    List<Vote> findAllByUserId(Long id);

}