package com.ssh.dartserver.domain.user.infra;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.RecommendationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT u FROM User u WHERE u.id = :userId")
    Optional<User> findByIdForUpdate(@Param("userId") Long userId);

    Optional<User> findByAuthInfo_Username(String username);

    Optional<User> findByRecommendationCode(RecommendationCode recommendationCode);

    @Query(value = "SELECT u FROM User u join fetch u.university WHERE u.id IN :ids")
    List<User> findAllByIdIn(@Param("ids") List<Long> ids);

    @Query(value = "SELECT f.user FROM Friend f WHERE f.friendUser.id = :userId")
    List<User> findAllAddedMeAsFriendByUserId(@Param("userId") Long id);

    @Query(value = "SELECT u FROM User u WHERE u.university.id= :universityId AND u.id != :userId")
    List<User> findAllSameDepartmentByUniversityId(@Param("universityId") Long universityId, @Param("userId") Long userId);

    @Query(value = "SELECT DISTINCT f2.friendUser " +
            "FROM Friend f1, Friend f2 " +
            "WHERE f1.user.id = :userId AND f1.friendUser.id = f2.user.id AND f2.friendUser.id != :userId")
    List<User> findAllFriendsOfFriendsByUserId(@Param("userId") Long id);

}
