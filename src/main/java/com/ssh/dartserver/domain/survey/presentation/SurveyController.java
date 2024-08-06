package com.ssh.dartserver.domain.survey.presentation;

import com.ssh.dartserver.domain.survey.presentation.request.AnswerRequest;
import com.ssh.dartserver.domain.survey.presentation.request.CommentRequest;
import com.ssh.dartserver.domain.survey.presentation.response.SurveyResponse;
import com.ssh.dartserver.domain.survey.application.AnswerService;
import com.ssh.dartserver.domain.survey.application.CommentService;
import com.ssh.dartserver.domain.survey.application.SurveyService;
import com.ssh.dartserver.global.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@Deprecated(since="20240724", forRemoval = true)
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

    @Deprecated(since = "20231108", forRemoval = true)
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

    @PostMapping("/{surveyId}/comments/{commentId}/likes")
    public ResponseEntity<Void> createCommentLike(Authentication authentication,
                                                  @PathVariable("surveyId") Long surveyId,
                                                  @PathVariable("commentId") Long commentId) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Long commentLikeId = commentService.createCommentLike(principal.getUser(), surveyId, commentId);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("v1/{surveyId}/comments/{commentId}/likes/{commentLikeId}")
                .buildAndExpand(surveyId, commentId, commentLikeId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PostMapping("/{surveyId}/comments/{commentId}/reports")
    public ResponseEntity<Void> createCommentReport(Authentication authentication,
                                                  @PathVariable("surveyId") Long surveyId,
                                                  @PathVariable("commentId") Long commentId) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Long commentReportId = commentService.createCommentReport(principal.getUser(), surveyId, commentId);

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("v1/{surveyId}/comments/{commentId}/reports/{commentReportId}")
                .buildAndExpand(surveyId, commentId, commentReportId)
                .toUri();

        return ResponseEntity.created(location).build();
    }


}
