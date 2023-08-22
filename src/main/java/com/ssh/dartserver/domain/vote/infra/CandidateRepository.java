package com.ssh.dartserver.domain.vote.infra;

import com.ssh.dartserver.domain.vote.domain.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
}
