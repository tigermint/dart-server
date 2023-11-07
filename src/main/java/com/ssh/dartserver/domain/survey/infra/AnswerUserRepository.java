package com.ssh.dartserver.domain.survey.infra;

import com.ssh.dartserver.domain.survey.domain.AnswerUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerUserRepository extends JpaRepository<AnswerUser, Long> {
    Optional<AnswerUser> findByUserIdAndAnswerId(Long userId, Long answerId);
}
