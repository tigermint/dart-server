package com.ssh.dartserver.domain.survey.infra;

import com.ssh.dartserver.domain.survey.domain.Comment;
import com.ssh.dartserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByIdAndSurveyId(Long commentId, Long surveyId);

    @Modifying
    @Transactional
    @Query("update Comment c set c.user = null where c.user = :user")
    void updateAllUserToNull(@Param("user") User user);
}
