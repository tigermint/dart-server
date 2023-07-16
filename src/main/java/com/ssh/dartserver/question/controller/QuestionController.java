package com.ssh.dartserver.question.controller;

import com.ssh.dartserver.question.dto.QuestionResponse;
import com.ssh.dartserver.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping("/v1/questions")
    public ResponseEntity<List<QuestionResponse>> list() {
        return ResponseEntity.ok(questionService.list());
    }
}
