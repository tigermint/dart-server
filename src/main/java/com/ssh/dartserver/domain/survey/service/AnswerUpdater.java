package com.ssh.dartserver.domain.survey.service;

import com.ssh.dartserver.domain.survey.infra.AnswerUserRepository;
import com.ssh.dartserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AnswerUpdater {
    private final AnswerUserRepository answerUserRepository;

    @Transactional
    public void updateAllUserToNullInAnswer(User user) {
        answerUserRepository.updateAllUserToNullInBatch(user);
    }
}
