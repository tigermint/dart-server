package com.ssh.dartserver.domain.survey.infra;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssh.dartserver.domain.survey.domain.Survey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static com.ssh.dartserver.domain.survey.domain.QAnswer.answer;
import static com.ssh.dartserver.domain.survey.domain.QAnswerUser.answerUser;
import static com.ssh.dartserver.domain.survey.domain.QCategory.category;
import static com.ssh.dartserver.domain.survey.domain.QComment.comment;
import static com.ssh.dartserver.domain.survey.domain.QSurvey.survey;

@RequiredArgsConstructor
public class SurveyRepositoryImpl implements SurveyRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Survey> findAllVisibleSurvey(Pageable pageable) {
        List<Survey> content = queryFactory
                .select(survey)
                .distinct()
                .from(survey)
                .join(survey.category, category).fetchJoin()
                .leftJoin(survey.answers, answer)
                .leftJoin(answer.answerUsers, answerUser)
                .leftJoin(survey.comments, comment)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(survey.countDistinct())
                .from(survey)
                .join(survey.category, category)
                .leftJoin(survey.answers, answer)
                .leftJoin(answer.answerUsers, answerUser)
                .leftJoin(survey.comments, comment);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
