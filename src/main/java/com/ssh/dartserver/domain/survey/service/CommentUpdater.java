package com.ssh.dartserver.domain.survey.service;

import com.ssh.dartserver.domain.survey.infra.CommentLikeRepository;
import com.ssh.dartserver.domain.survey.infra.CommentReportRepository;
import com.ssh.dartserver.domain.survey.infra.CommentRepository;
import com.ssh.dartserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CommentUpdater {
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final CommentReportRepository commentReportRepository;

    @Transactional
    public void updateAllUserToNullInCommentAndRelatedData(User user) {
        commentRepository.updateAllUserToNull(user);
        commentLikeRepository.updateAllUserToNull(user);
        commentReportRepository.updateAllUserToNull(user);
    }
}
