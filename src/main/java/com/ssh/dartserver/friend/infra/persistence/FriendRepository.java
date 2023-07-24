package com.ssh.dartserver.friend.infra.persistence;

import com.ssh.dartserver.friend.domain.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long>{
    List<Friend> findAllByUserId(Long userId);
    Optional<Friend> findByUserIdAndFriendUserId(Long userId, Long friendUserId);

    @Query(value  = "select a.friend_user_id\n" +
            "from (select f.friend_user_id\n" +
            "    from friend f, (select * from friend where friend.user_id = :userId) as r\n" +
            "    where f.user_id = r.friend_user_id) as a\n" +
            "left join\n" +
            "(select friend_user_id from friend where user_id = :userId) as b on a.friend_user_id = b.friend_user_id\n" +
            "where b.friend_user_id is null", nativeQuery = true)
    List<Long> findAllFriendsOfFriendsById(@Param("userId") Long userId);


    void deleteAllByUserIdAndFriendUserId(Long userId, Long friendUserId);
}
