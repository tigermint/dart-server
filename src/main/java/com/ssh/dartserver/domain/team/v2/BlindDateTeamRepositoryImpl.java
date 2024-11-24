package com.ssh.dartserver.domain.team.v2;

import static com.ssh.dartserver.domain.team.domain.QTeam.team;
import static com.ssh.dartserver.domain.team.domain.QTeamImage.teamImage;
import static com.ssh.dartserver.domain.team.domain.QTeamRegion.teamRegion;
import static com.ssh.dartserver.domain.team.domain.QTeamUser.teamUser;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.user.domain.QUser;
import com.ssh.dartserver.domain.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class BlindDateTeamRepositoryImpl implements BlindDateTeamRepository {
    private static final int ALL_REGION = 0;
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Team> findAll(User currentUser, Pageable pageable) {  // Pageable이 아닌 다른 Condition 객체를 만들어 제어할 필요가 있음
        List<Team> teams = queryFactory.select(team).distinct()  // ..? 중복이 생길 수가 있나?
                .from(team)
                .leftJoin(team.teamUsers, teamUser)
                .leftJoin(teamUser.user)
                .leftJoin(teamUser.user.university)
                .leftJoin(team.leader)
                .leftJoin(team.leader.university)
                .leftJoin(team.teamRegions, teamRegion)
                .leftJoin(team.teamImages, teamImage)
                .where(
                        notEqLeaderGender(currentUser).or(
                                notEqTeamUserGender(currentUser)
                        ),
                        team.isVisibleToSameUniversity.isTrue()
                                .or(
                                        notEqLeaderUniversity(currentUser)
//                                                .or(  // FIXME 왜 이걸 넣으면 출력이 empty가 될까요?
//                                                        notEqTeamUniversity(currentUser)
//                                                )
                                )
                ).orderBy(new OrderSpecifier<>(Order.DESC, team.createdTime)).offset(pageable.getOffset())
                .limit(pageable.getPageSize()).fetch();

        Long count = queryFactory.select(team.countDistinct())
                .from(team)
                .leftJoin(team.teamUsers, teamUser)
                .leftJoin(teamUser.user)  // count 쿼리에서는 fetchJoin 불필요
                .leftJoin(teamUser.user.university)
                .leftJoin(team.leader)
                .leftJoin(team.leader.university)
                .where(
                        notEqLeaderGender(currentUser).or(
                                notEqTeamUserGender(currentUser)
                        ),
                        team.isVisibleToSameUniversity.isTrue()
                                .or(
                                        notEqLeaderUniversity(currentUser)
//                                                .or(  // FIXME 왜 이걸 넣으면 출력이 empty가 될까요?
//                                                        notEqTeamUniversity(currentUser)
//                                                )
                                )
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(teams, pageable, () -> count);
    }

    private BooleanExpression notEqLeaderGender(User currentUser) {
        BooleanExpression leaderExists = team.leader.isNotNull();
        BooleanExpression genderNotEqual = team.leader.personalInfo.gender.ne(
                currentUser.getPersonalInfo().getGender());

//        if (leaderExists.isNull().equals(true)) return null;
        return leaderExists.and(genderNotEqual);
    }

    private BooleanExpression notEqTeamUserGender(User currentUser) {
        BooleanExpression teamUserExists = team.teamUsers.isNotEmpty();
        BooleanExpression genderNotEqual = teamUser.user.personalInfo.gender.ne(
                currentUser.getPersonalInfo().getGender());

        return teamUserExists.and(genderNotEqual);
    }

    private BooleanExpression notEqLeaderUniversity(User currentUser) {
        BooleanExpression leaderExists = team.leader.isNotNull();
        BooleanExpression universityNotEqual = team.leader.university.name.ne(currentUser.getUniversity().getName());

        return leaderExists.and(universityNotEqual);
    }

    private BooleanExpression notEqTeamUniversity(User currentUser) {
        BooleanExpression universityExists = team.university.isNotNull();
        BooleanExpression universityNotEqual = team.university.name.ne(currentUser.getUniversity().getName());

        return universityExists.and(universityNotEqual);
    }

}
