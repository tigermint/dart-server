package com.ssh.dartserver.domain.question.infra;

import com.ssh.dartserver.domain.question.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query(value = "SELECT * FROM question ORDER BY RAND() LIMIT 8", nativeQuery = true)
    List<Question> findRandomQuestions();
}
