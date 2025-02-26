package com.ssh.dartserver.domain.survey.infra;

import com.ssh.dartserver.domain.survey.domain.AnswerUser;
import com.ssh.dartserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface AnswerUserRepository extends JpaRepository<AnswerUser, Long> {
    Optional<AnswerUser> findByUserIdAndAnswerId(Long userId, Long answerId);

    @Modifying
    @Transactional
    @Query("update AnswerUser au set au.user = null where au.user = :user")
    void updateAllUserToNullInBatch(@Param("user") User user);
}
