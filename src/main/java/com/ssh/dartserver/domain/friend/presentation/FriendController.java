package com.ssh.dartserver.domain.friend.presentation;

import com.ssh.dartserver.global.auth.service.oauth.PrincipalDetails;
import com.ssh.dartserver.domain.friend.service.FriendService;
import com.ssh.dartserver.domain.friend.dto.FriendRecommendationCodeRequest;
import com.ssh.dartserver.domain.friend.dto.FriendRequest;
import com.ssh.dartserver.domain.friend.dto.FriendResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
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
    public ResponseEntity<String> create(Authentication authentication, @RequestBody @Valid FriendRequest request){
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Long friendUserId = friendService.createFriendById(principal.getUser(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(friendUserId)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * 추천인 코드로 친구 추가
     * @param authentication
     * @param request
     * @return
     */

    @PostMapping("/invite")
    public ResponseEntity<FriendResponse> createFriendByRecommendationCode(Authentication authentication, @RequestBody @Valid FriendRecommendationCodeRequest request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Long friendUserId = friendService.createFriendByRecommendationCode(principal.getUser(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(friendUserId)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * 친구 목록 조회 suggested = false
     * 알 수도 있는 친구 목록 조회 suggested = true
     * @param authentication
     * @param suggested
     * @return
     */

    @GetMapping
    public ResponseEntity<List<FriendResponse>> list(Authentication authentication, @RequestParam(defaultValue = "false") boolean suggested) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        if(suggested) {
            return ResponseEntity.ok(friendService.listPossibleFriend(principal.getUser()));
        }
        return ResponseEntity.ok(friendService.listFriend(principal.getUser()));
    }

    /**
     * 친구 삭제
     * @param authentication
     * 
     * @param friendUserId
     * @return
     */
    @DeleteMapping
    public ResponseEntity<String> delete(Authentication authentication, @RequestParam @NotNull Long friendUserId ) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        friendService.delete(principal.getUser(), friendUserId);
        return ResponseEntity.noContent().build();
    }

}