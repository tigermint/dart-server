package com.ssh.dartserver.domain.user.infra;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.recommendcode.RecommendationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findAllByUniversityId(Long universityId);

    Optional<User> findByRecommendationCode(RecommendationCode recommendationCode);

    Optional<User> findById(Long id);
}
