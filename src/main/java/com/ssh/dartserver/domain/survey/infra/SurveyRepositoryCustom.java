package com.ssh.dartserver.domain.survey.infra;

import com.ssh.dartserver.domain.survey.domain.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SurveyRepositoryCustom {
    Page<Survey> findAllVisibleSurvey(Pageable pageable);
}
