package com.ssh.dartserver.domain.vote.infra;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.vote.domain.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    @Query("select v " +
            "from Vote v " +
            "join fetch v.candidates.values cv join fetch cv.user u join fetch u.university join fetch v.question " +
            "where v.pickedUser = :pickedUser and v.id = :voteId")
    Optional<Vote> findByPickedUserAndId(@Param("pickedUser") User pickedUser, @Param("voteId") Long voteId);

    @Query("select distinct v " +
            "from Vote v " +
            "join fetch v.candidates.values cv " +
            "join fetch cv.user u " +
            "join fetch u.university " +
            "join fetch v.question " +
            "where v.pickedUser = :pickedUser")
    List<Vote> findAllByPickedUser(@Param("pickedUser") User pickedUser);

    @Query(value = "select v " +
            "from Vote v " +
            "join fetch v.question " +
            "join fetch v.pickingUser u " +
            "join fetch u.university " +
            "where v.pickedUser = :pickedUser",
            countQuery = "select count(v) from Vote v where v.pickedUser = :pickedUser")
    Page<Vote> findAllByPickedUser(@Param("pickedUser") User pickedUser, Pageable pageable);

    @Modifying
    @Query("update Vote v set v.pickingUser = null where v.pickingUser = :user")
    void updateAllPickingUserToNull(@Param("user") User user);

    @Modifying
    @Query("delete from Vote v where v in :pickedUserVotes")
    void deleteAllByVoteIn(@Param("pickedUserVotes") List<Vote> pickedUserVotes);
}