package com.ssh.dartserver.domain.survey.service;

import com.ssh.dartserver.domain.survey.domain.Comment;
import com.ssh.dartserver.domain.survey.domain.Content;
import com.ssh.dartserver.domain.survey.domain.Survey;
import com.ssh.dartserver.domain.survey.dto.CommentRequest;
import com.ssh.dartserver.domain.survey.infra.CommentRepository;
import com.ssh.dartserver.domain.survey.infra.SurveyRepository;
import com.ssh.dartserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final SurveyRepository surveyRepository;
    private final CommentRepository commentRepository;
    public Long createComment(User user, Long surveyId, CommentRequest request) {
        Survey survey = surveyRepository.findSurveyById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 설문입니다."));

        Comment comment = Comment.builder()
                .content(Content.from(request.getContent()))
                .user(user)
                .survey(survey)
                .build();
        return commentRepository.save(comment).getId();
    }

    public void deleteComment(User user, Long surveyId, Long commentId) {
        Comment comment = commentRepository.findByIdAndSurveyId(commentId, surveyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
        validateCommentAuthor(user, comment);
        commentRepository.delete(comment);
    }

    private static void validateCommentAuthor(User user, Comment comment) {
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("댓글 작성자만 삭제할 수 있습니다.");
        }
    }
}
