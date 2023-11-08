package com.ssh.dartserver.domain.survey.infra;

import com.ssh.dartserver.domain.survey.domain.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long>, SurveyRepositoryCustom {

    @Query("select s " +
            "from Survey s " +
            "join fetch s.category ct " +
            "left join s.answers a " +
            "left join a.answerUsers au " +
            "left join au.user auu " +
            "left join s.comments cm " +
            "left join cm.user cmu " +
            "left join cm.commentLikes cml " +
            "left join cm.commentReports cmr " +
            "where s.id = :surveyId")
    Optional<Survey> findById(@Param("surveyId") Long surveyId);

}
