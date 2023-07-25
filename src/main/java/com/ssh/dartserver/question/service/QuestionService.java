package com.ssh.dartserver.question.service;

import com.ssh.dartserver.question.domain.Question;
import com.ssh.dartserver.question.dto.QuestionResponse;
import com.ssh.dartserver.question.dto.mapper.QuestionMapper;
import com.ssh.dartserver.question.infra.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;

    public List<QuestionResponse> list() {
        List<Question> questions = questionRepository.findRandomQuestions();
        return questions.stream()
                .map(questionMapper::toDto)
                .collect(Collectors.toList());
    }
}
