package com.ssh.dartserver.domain.survey.infra;

import com.ssh.dartserver.domain.survey.domain.CommentLike;
import com.ssh.dartserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long>{
    Optional<CommentLike> findByCommentIdAndUserId(Long commentId, Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE CommentLike cl SET cl.user = null WHERE cl.user = :user")
    void updateAllUserToNull(@Param("user") User user);
}
