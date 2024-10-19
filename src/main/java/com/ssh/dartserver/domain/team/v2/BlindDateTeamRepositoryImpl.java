package com.ssh.dartserver.domain.team.v2;

import static com.ssh.dartserver.domain.team.domain.QTeam.team;
import static com.ssh.dartserver.domain.team.domain.QTeamImage.teamImage;
import static com.ssh.dartserver.domain.team.domain.QTeamRegion.teamRegion;
import static com.ssh.dartserver.domain.user.domain.QUser.user;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssh.dartserver.domain.team.domain.Team;
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
        List<Team> teams = queryFactory.select(team)
                .distinct()  // ..? 중복이 생길 수가 있나?
                .from(team)
                .leftJoin(team.leader, user)
                .leftJoin(team.teamRegions, teamRegion)
                .leftJoin(team.teamImages, teamImage)
                .where(
                        team.leader.personalInfo.gender.ne(currentUser.getPersonalInfo().getGender()),
                        team.isVisibleToSameUniversity.isTrue()
                            .or(team.isVisibleToSameUniversity.isFalse().and(team.leader.university.name.ne(currentUser.getUniversity().getName())))
                )
                .orderBy(new OrderSpecifier<>(Order.DESC, team.createdTime))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory.select(team.countDistinct())
                .from(team)
                .leftJoin(team.leader, user)
                .leftJoin(team.teamRegions, teamRegion)
                .leftJoin(team.teamImages, teamImage)
                .where(
                        team.leader.personalInfo.gender.ne(currentUser.getPersonalInfo().getGender()),
                        team.isVisibleToSameUniversity.isTrue()
                                .or(team.isVisibleToSameUniversity.isFalse().and(team.leader.university.name.ne(currentUser.getUniversity().getName())))
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(teams, pageable, () -> count);
    }

}
