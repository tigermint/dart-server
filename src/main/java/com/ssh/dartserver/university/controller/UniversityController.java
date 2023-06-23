package com.ssh.dartserver.university.controller;

import com.ssh.dartserver.university.dto.UniversityDto;
import com.ssh.dartserver.university.service.UniversityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class UniversityController {
    private final UniversityService universityService;

    @GetMapping("/v1/university")
    public ResponseEntity<List<UniversityDto>> readAllUniversities() {
        return ResponseEntity.ok(universityService.readAllUniversities());
    }
}
