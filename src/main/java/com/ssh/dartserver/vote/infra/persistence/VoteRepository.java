package com.ssh.dartserver.vote.infra.persistence;

import com.ssh.dartserver.vote.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findAllByUserId(Long userId);
}