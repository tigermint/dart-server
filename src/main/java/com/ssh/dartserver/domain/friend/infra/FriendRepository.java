package com.ssh.dartserver.domain.friend.infra;

import com.ssh.dartserver.domain.friend.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long>{
    Optional<Friend> findByUserIdAndFriendUserId(Long userId, Long friendUserId);
    List<Friend> findAllByUserId(Long userId);
    void deleteAllByUserIdOrFriendUserId(Long userId, Long friendUserId);

}
