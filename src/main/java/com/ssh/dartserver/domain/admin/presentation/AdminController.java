package com.ssh.dartserver.domain.admin.presentation;

import com.ssh.dartserver.domain.admin.service.AdminIdCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
@Slf4j
public class AdminController {
    private final AdminIdCardService adminIdCardService;

    @GetMapping("/verify-id-card")
    public ResponseEntity<String> verifyIdCard(@RequestParam("id") String userId, @RequestParam("sign") String sign) {
        adminIdCardService.verifyIdCard(Long.valueOf(userId), sign);
        return ResponseEntity.ok("done");
    }
}
