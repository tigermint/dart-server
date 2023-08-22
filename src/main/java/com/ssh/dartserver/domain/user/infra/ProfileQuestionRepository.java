package com.ssh.dartserver.domain.user.infra;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.profilequestions.ProfileQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileQuestionRepository extends JpaRepository<ProfileQuestion, Long> {
    List<ProfileQuestion> findAllByUser(User user);
    void deleteAllByUser(User user);
}
