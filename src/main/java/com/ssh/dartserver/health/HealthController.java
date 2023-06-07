package com.ssh.dartserver.health;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/health")
public class HealthController {
    @GetMapping( path="")
    String checkHealth(@RequestParam(defaultValue = "") String word) {
        return "dart health check "+ word + ":" + LocalDateTime.now();
    }
}
