package com.ssh.dartserver.domain.user.infra;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.profilequestions.ProfileQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProfileQuestionRepository extends JpaRepository<ProfileQuestion, Long> {
    @Query("select distinct pq from ProfileQuestion pq join fetch pq.question q where pq.user = :user")
    List<ProfileQuestion> findAllByUser(@Param("user") User user);

    void deleteAllByUser(User user);
}
