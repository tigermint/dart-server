package com.ssh.dartserver.domain.team.infra;

import static com.ssh.dartserver.domain.team.domain.QTeam.team;
import static com.ssh.dartserver.domain.team.domain.QTeamRegion.teamRegion;
import static com.ssh.dartserver.domain.team.domain.QTeamUser.teamUser;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssh.dartserver.domain.team.domain.QTeam;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;

@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Team> findAllVisibleTeams(Long myUniversityId, Gender myGender, Pageable pageable) {
        Predicate predicate = teamUser.team.isVisibleToSameUniversity.isTrue()
                .or(
                        teamUser.team.isVisibleToSameUniversity.isFalse()
                                .and(teamUser.team.university.id.ne(myUniversityId))
                )
                .and(teamUser.user.personalInfo.gender.ne(myGender));

        List<Long> teamIds = queryFactory
                .select(teamUser.team.id)
                .distinct()
                .from(teamUser)
                .join(teamRegion).on(teamUser.team.eq(teamRegion.team))
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Team> teams = queryFactory.selectFrom(QTeam.team)
                .where(QTeam.team.id.in(teamIds))
                .orderBy(new OrderSpecifier(Order.DESC, QTeam.team.id))
                .fetch();

        long total = queryFactory
                .selectDistinct(teamUser.team)
                .from(teamUser)
                .join(teamRegion).on(teamUser.team.eq(teamRegion.team))
                .where(predicate)
                .fetchCount();

        return new PageImpl<>(teams, pageable, total);
    }

    @Override
    public Page<Team> findAllVisibleTeamsByRegionId(Long myUniversityId, Gender myGender, Long regionId, Pageable pageable) {
        Predicate predicate = teamUser.team.isVisibleToSameUniversity.isTrue()
                .or(
                        teamUser.team.isVisibleToSameUniversity.isFalse()
                                .and(teamUser.team.university.id.ne(myUniversityId))
                )
                .and(teamUser.user.personalInfo.gender.ne(myGender))
                .and(teamRegion.region.id.eq(regionId));

        List<Long> teamIds = queryFactory
                .select(teamUser.team.id)
                .distinct()
                .from(teamUser)
                .join(teamRegion).on(teamUser.team.eq(teamRegion.team))
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Team> teams = queryFactory.selectFrom(QTeam.team)
                .where(QTeam.team.id.in(teamIds))
                .orderBy(new OrderSpecifier(Order.DESC, QTeam.team.id))
                .fetch();

        long total = queryFactory
                .selectDistinct(teamUser.team)
                .from(teamUser)
                .join(teamRegion).on(teamUser.team.eq(teamRegion.team))
                .where(predicate)
                .fetchCount();

        return new PageImpl<>(teams, pageable, total);
    }
}
