package com.ssh.dartserver.domain.survey.application;

import com.ssh.dartserver.domain.survey.domain.*;
import com.ssh.dartserver.domain.survey.presentation.request.CommentRequest;
import com.ssh.dartserver.domain.survey.infra.CommentLikeRepository;
import com.ssh.dartserver.domain.survey.infra.CommentReportRepository;
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
    private final CommentLikeRepository commentLikeRepository;
    private final CommentReportRepository commentReportRepository;
    public Long createComment(User user, Long surveyId, CommentRequest request) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 설문입니다."));

        Comment comment = Comment.builder()
                .content(Content.from(request.getContent()))
                .user(user)
                .survey(survey)
                .build();
        return commentRepository.save(comment).getId();
    }

    public void deleteComment(User user, Long surveyId, Long commentId) {
        Comment comment = getComment(surveyId, commentId);
        validateCommentAuthor(user, comment);
        commentRepository.delete(comment);
    }

    public Long createCommentLike(User user, Long surveyId, Long commentId) {
        Comment comment = getComment(surveyId, commentId);
        validateLikedComment(user, commentId);
        CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .user(user)
                .build();
        return commentLikeRepository.save(commentLike).getId();
    }

    public Long createCommentReport(User user, Long surveyId, Long commentId) {
        Comment comment = getComment(surveyId, commentId);
        validateReportedComment(user, commentId);
        CommentReport commentReport = CommentReport.builder()
                .comment(comment)
                .user(user)
                .build();
        return commentReportRepository.save(commentReport).getId();
    }
    private Comment getComment(Long surveyId, Long commentId) {
        return commentRepository.findByIdAndSurveyId(commentId, surveyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
    }
    private void validateLikedComment(User user, Long commentId) {
        commentLikeRepository.findByCommentIdAndUserId(commentId, user.getId())
                .ifPresent(commentReport -> {
                    throw new IllegalArgumentException("이미 좋아요 한 댓글입니다.");
                });
    }

    private void validateReportedComment(User user, Long commentId) {
        commentReportRepository.findByCommentIdAndUserId(commentId, user.getId())
                .ifPresent(commentReport -> {
                    throw new IllegalArgumentException("이미 신고한 댓글입니다.");
                });
    }

    private static void validateCommentAuthor(User user, Comment comment) {
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("댓글 작성자만 삭제할 수 있습니다.");
        }
    }
}
