package com.ssh.dartserver.friend.controller;

import com.ssh.dartserver.auth.service.oauth.PrincipalDetails;
import com.ssh.dartserver.friend.dto.FriendRequestDto;
import com.ssh.dartserver.friend.dto.FriendResponseDto;
import com.ssh.dartserver.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/friends")
public class FriendController {
    private final FriendService friendService;

    /**
     * 친구 추가
     * @param authentication
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<String> create(Authentication authentication, @RequestBody @Valid FriendRequestDto request){
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        friendService.create(principal.getUser(), request);
        return ResponseEntity.ok("친구 추가 성공");
    }

    /**
     * 친구 목록 조회 suggested = false
     * 알 수도 있는 친구 목록 조회 suggested = true
     * @param authentication
     * @param suggested
     * @return
     */

    @GetMapping
    public ResponseEntity<List<FriendResponseDto>> list(Authentication authentication, @RequestParam(defaultValue = "false") boolean suggested) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        if(suggested) {
            return ResponseEntity.ok(friendService.requiredList(principal.getUser()));
        }
        return ResponseEntity.ok(friendService.list(principal.getUser()));
    }

    /**
     * 친구 삭제
     * @param authentication
     * @param friendUserId
     * @return
     */
    @DeleteMapping
    public ResponseEntity<String> delete(Authentication authentication, @RequestParam @NotNull Long friendUserId ) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        friendService.delete(principal.getUser(), friendUserId);
        return ResponseEntity.ok("친구 삭제 성공");
    }

}