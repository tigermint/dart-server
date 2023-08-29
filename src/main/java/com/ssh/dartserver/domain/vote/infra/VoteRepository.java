package com.ssh.dartserver.domain.vote.infra;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.vote.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
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
            "where v.pickingUser = :pickingUser and v.id = :voteId")
    Optional<Vote> findByPickedUserAndId(@Param("pickingUser") User pickedUser, @Param("voteId") Long voteId);

    List<Vote> findAllByPickingUser(User pickingUser);
    @Query("select distinct v " +
            "from Vote v " +
            "join fetch v.candidates.values cv join fetch cv.user u join fetch u.university join fetch v.question " +
            "where v.pickedUser = :pickedUser")
    List<Vote> findAllByPickedUser(@Param("pickedUser") User pickedUser);
}