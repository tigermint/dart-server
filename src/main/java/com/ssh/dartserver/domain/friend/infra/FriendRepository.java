package com.ssh.dartserver.domain.friend.infra;

import com.ssh.dartserver.domain.friend.domain.Friend;
import com.ssh.dartserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long>{
    Optional<Friend> findByUserIdAndFriendUserId(Long userId, Long friendUserId);
    Optional<Friend> findByUserIdAndId(Long userId, Long friendId);
    List<Friend> findAllByUserId(Long userId);
    void deleteAllByUserOrFriendUser(User user, User friendUser);

}
