package com.ssh.dartserver.domain.survey.presentation;

import com.ssh.dartserver.domain.survey.dto.AnswerRequest;
import com.ssh.dartserver.domain.survey.dto.CommentRequest;
import com.ssh.dartserver.domain.survey.dto.SurveyResponse;
import com.ssh.dartserver.domain.survey.service.AnswerService;
import com.ssh.dartserver.domain.survey.service.CommentService;
import com.ssh.dartserver.domain.survey.service.SurveyService;
import com.ssh.dartserver.global.auth.service.oauth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/v1/surveys")
@RequiredArgsConstructor
public class SurveyController {
    private final SurveyService surveyService;
    private final AnswerService answerService;
    private final CommentService commentService;

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

    @PostMapping("/{surveyId}/answers")
    public ResponseEntity<SurveyResponse.ReadDto> createAnswer(Authentication authentication,
                                                               @PathVariable("surveyId") Long surveyId,
                                                               @RequestBody @Valid AnswerRequest request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(answerService.createAnswer(principal.getUser(), surveyId, request));
    }

    @PatchMapping("/{surveyId}/answers/{answerId}")
    public ResponseEntity<SurveyResponse.ReadDto> updateAnswer(Authentication authentication,
                                                               @PathVariable("surveyId") Long surveyId,
                                                               @PathVariable("answerId") Long answerId,
                                                               @RequestBody @Valid AnswerRequest request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(answerService.updateAnswer(principal.getUser(), surveyId, answerId, request));
    }

    @PostMapping("/{surveyId}/comments")
    public ResponseEntity<Void> createComment(Authentication authentication,
                                              @PathVariable("surveyId") Long surveyId,
                                              @RequestBody @Valid CommentRequest request) {

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Long commentId = commentService.createComment(principal.getUser(), surveyId, request);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("v1/{surveyId}/comments/{commentId}")
                .buildAndExpand(surveyId, commentId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{surveyId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(Authentication authentication,
                                              @PathVariable Long surveyId,
                                              @PathVariable("commentId") Long commentId) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        commentService.deleteComment(principal.getUser(),surveyId, commentId);
        return ResponseEntity.noContent().build();
    }
}
