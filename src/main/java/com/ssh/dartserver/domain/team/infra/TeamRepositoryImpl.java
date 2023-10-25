package com.ssh.dartserver.domain.team.infra;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.dto.OrderMethod;
import com.ssh.dartserver.domain.team.dto.TeamSearchCondition;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.studentverificationinfo.StudentIdCardVerificationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.ssh.dartserver.domain.proposal.domain.QProposal.proposal;
import static com.ssh.dartserver.domain.team.domain.QSingleTeamFriend.singleTeamFriend;
import static com.ssh.dartserver.domain.team.domain.QTeam.team;
import static com.ssh.dartserver.domain.team.domain.QTeamRegion.teamRegion;
import static com.ssh.dartserver.domain.team.domain.QTeamUser.teamUser;
import static com.ssh.dartserver.domain.user.domain.QUser.user;

@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamRepositoryCustom {
    private static final int ALL_REGION = 0;
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Team> findAllVisibleTeam(User currentUser, TeamSearchCondition condition, Pageable pageable) {
        List<Team> content = queryFactory
                .select(team)
                .distinct()
                .from(team)
                .join(team.teamUsers, teamUser)
                .join(team.teamRegions, teamRegion)
                .join(teamUser.user, user)
                .join(team.singleTeamFriends, singleTeamFriend)
                .leftJoin(team.requestedTeamProposals, proposal)
                .where(
                        teamUser.user.personalInfo.gender.ne(currentUser.getPersonalInfo().getGender()),
                        isVisibleToSameUniversityEq(currentUser.getUniversity().getId()),
                        verifiedStudentEq(condition.getVerifiedStudent()),
                        hasProfileImageEq(condition.getHasProfileImage()),
                        regionEq(condition.getRegionId())
                )
                .orderBy(createOrderSpecifier(condition.getOrder()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(team.countDistinct())
                .from(team)
                .join(team.teamUsers, teamUser)
                .join(team.teamRegions, teamRegion)
                .join(teamUser.user, user)
                .join(team.singleTeamFriends, singleTeamFriend)
                .where(
                        teamUser.user.personalInfo.gender.ne(currentUser.getPersonalInfo().getGender()),
                        isVisibleToSameUniversityEq(currentUser.getUniversity().getId()),
                        verifiedStudentEq(condition.getVerifiedStudent()),
                        hasProfileImageEq(condition.getHasProfileImage()),
                        regionEq(condition.getRegionId())
                );
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private static OrderSpecifier<?>[] createOrderSpecifier(OrderMethod order) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if (order == null || order.equals(OrderMethod.LATEST))
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, team.createdTime));
        else if (order.equals(OrderMethod.LIKE)) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, team.requestedTeamProposals.size()));
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, team.createdTime));
        } else if (order.equals(OrderMethod.VIEW)) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, team.viewCount.value));
        } else {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, team.createdTime));
        }
        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

    private BooleanExpression hasProfileImageEq(Boolean hasProfileImage) {
        if (hasProfileImage != null) {
            return hasProfileImage.equals(Boolean.TRUE)
                    ? user.personalInfo.profileImageUrl.value.ne("DEFAULT")
                    .or(singleTeamFriend.profileImageUrl.value.ne("DEFAULT"))
                    : null;
        }
        return null;
    }

    private BooleanExpression isVisibleToSameUniversityEq(Long universityId) {
        return team.isVisibleToSameUniversity.isTrue()
                .or(team.isVisibleToSameUniversity.isFalse().and(team.university.id.ne(universityId)));
    }

    private BooleanExpression verifiedStudentEq(Boolean verifiedStudent) {
        if (verifiedStudent != null) {
            return verifiedStudent.equals(Boolean.TRUE)
                    ? user.studentVerificationInfo.studentIdCardVerificationStatus.eq(StudentIdCardVerificationStatus.VERIFICATION_SUCCESS)
                    : null;
        }
        return null;
    }

    //TODO: 전체 지역 로직 수정 필요
    private BooleanExpression regionEq(Long regionId) {
        if (regionId != null) {
            if (regionId == ALL_REGION) {
                return null;
            }else{
                return teamRegion.region.id.eq(regionId);
            }
        }
        return null;
    }
}
