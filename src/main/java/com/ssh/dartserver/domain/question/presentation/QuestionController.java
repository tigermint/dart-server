package com.ssh.dartserver.domain.question.presentation;

import com.ssh.dartserver.domain.question.presentation.response.QuestionResponse;
import com.ssh.dartserver.domain.question.application.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Deprecated(since="20240724", forRemoval = true)
@RestController
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping("/v1/questions")
    public ResponseEntity<List<QuestionResponse>> list() {
        return ResponseEntity.ok(questionService.list());
    }
}
