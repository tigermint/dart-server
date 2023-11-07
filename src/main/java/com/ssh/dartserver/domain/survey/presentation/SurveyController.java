package com.ssh.dartserver.domain.survey.presentation;

import com.ssh.dartserver.domain.survey.dto.SurveyResponse;
import com.ssh.dartserver.domain.survey.service.SurveyService;
import com.ssh.dartserver.global.auth.service.oauth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/surveys")
@RequiredArgsConstructor
public class SurveyController {
    private final SurveyService surveyService;

    @GetMapping("/{surveyId}")
    public ResponseEntity<SurveyResponse.ReadDto> readSurvey(Authentication authentication, @PathVariable("surveyId") Long surveyId) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(surveyService.readSurvey(principal.getUser(), surveyId));
    }

    @GetMapping
    public ResponseEntity<Page<SurveyResponse.ListDto>> listSurvey(Authentication authentication, Pageable pageable) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(surveyService.listSurvey(principal.getUser(), pageable));
    }
}
