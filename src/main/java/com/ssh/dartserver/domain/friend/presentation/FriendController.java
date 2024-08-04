package com.ssh.dartserver.domain.friend.presentation;

import com.ssh.dartserver.domain.friend.application.FriendService;
import com.ssh.dartserver.domain.friend.presentation.request.FriendRecommendationCodeRequest;
import com.ssh.dartserver.domain.friend.presentation.request.FriendRequest;
import com.ssh.dartserver.domain.friend.presentation.response.FriendResponse;
import com.ssh.dartserver.global.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

@Deprecated(since="20240724")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/friends")
public class FriendController {

    private final FriendService friendService;

    @PostMapping
    public ResponseEntity<String> create(Authentication authentication, @RequestBody @Valid FriendRequest request){
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Long friendId = friendService.createFriendById(principal.getUser(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(friendId)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/invite")
    public ResponseEntity<FriendResponse> createFriendByRecommendationCode(Authentication authentication, @RequestBody @Valid FriendRecommendationCodeRequest request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Long friendId = friendService.createFriendByRecommendationCode(principal.getUser(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/v1/friends/{id}")
                .buildAndExpand(friendId)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{friendId}")
    public ResponseEntity<FriendResponse> readFriend(Authentication authentication, @PathVariable Long friendId) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(friendService.readFriend(principal.getUser(), friendId));
    }

    @GetMapping
    public ResponseEntity<List<FriendResponse>> list(Authentication authentication, @RequestParam(defaultValue = "false") boolean suggested) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        if(suggested) {
            return ResponseEntity.ok(friendService.listPossibleFriend(principal.getUser()));
        }
        return ResponseEntity.ok(friendService.listFriend(principal.getUser()));
    }

    @DeleteMapping
    public ResponseEntity<String> delete(Authentication authentication, @RequestParam @NotNull Long friendUserId ) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        friendService.delete(principal.getUser(), friendUserId);
        return ResponseEntity.noContent().build();
    }

}