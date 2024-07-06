package com.ssh.dartserver.domain.university.presentation;

import com.ssh.dartserver.domain.university.dto.UniversityResponse;
import com.ssh.dartserver.domain.university.dto.UniversitySearchRequest;
import com.ssh.dartserver.domain.university.service.UniversityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class UniversityController {
    private final UniversityService universityService;

    @GetMapping("/v1/universities")
    public ResponseEntity<List<UniversityResponse>> search(@ModelAttribute @Valid UniversitySearchRequest request) {
        List<UniversityResponse> universityResponses = universityService.search(request);
        return ResponseEntity.ok(universityResponses);
    }
}
