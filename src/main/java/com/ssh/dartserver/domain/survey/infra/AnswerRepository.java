package com.ssh.dartserver.domain.survey.infra;

import com.ssh.dartserver.domain.survey.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
