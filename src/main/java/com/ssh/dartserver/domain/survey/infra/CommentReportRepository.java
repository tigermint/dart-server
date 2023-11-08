package com.ssh.dartserver.domain.survey.infra;

import com.ssh.dartserver.domain.survey.domain.CommentReport;
import com.ssh.dartserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
    Optional<CommentReport> findByCommentIdAndUserId(Long commentId, Long userId);

    @Modifying
    @Query("UPDATE CommentReport cr SET cr.user = null WHERE cr.user = :user")
    void updateAllUserToNull(@Param("user") User user);
}
