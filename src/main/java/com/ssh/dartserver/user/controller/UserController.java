package com.ssh.dartserver.user.controller;

import com.ssh.dartserver.auth.service.oauth.PrincipalDetails;
import com.ssh.dartserver.user.dto.UserRequestDto;
import com.ssh.dartserver.user.dto.UserWithUniversityResponseDto;
import com.ssh.dartserver.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserWithUniversityResponseDto> signup(Authentication authentication, @Valid @RequestBody UserRequestDto request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(userService.completeSignupWithRecommendationCode(principal.getUser(), request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserWithUniversityResponseDto> read(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(userService.read(principal.getUser().getId()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserWithUniversityResponseDto> update(Authentication authentication, @Valid @RequestBody UserRequestDto request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(userService.updateUserInformation(principal.getUser(), request));
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> delete(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        userService.delete(principal.getUser());
        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
    }
}
