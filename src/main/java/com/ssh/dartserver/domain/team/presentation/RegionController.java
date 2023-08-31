package com.ssh.dartserver.domain.team.presentation;

import com.ssh.dartserver.domain.team.dto.RegionResponse;
import com.ssh.dartserver.domain.team.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/regions")
public class RegionController {
    private final RegionService regionService;

    @GetMapping
    public ResponseEntity<List<RegionResponse>> listRegion() {
        return ResponseEntity.ok(regionService.listRegion());
    }

}
