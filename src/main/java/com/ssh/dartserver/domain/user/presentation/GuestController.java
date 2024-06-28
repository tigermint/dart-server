package com.ssh.dartserver.domain.user.presentation;

import com.ssh.dartserver.domain.user.dto.GuestInviteRequest;
import com.ssh.dartserver.domain.user.service.GuestService;
import java.net.URI;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Deprecated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/guests")
public class GuestController {
    private final GuestService guestService;

    @PostMapping("")
    public ResponseEntity<String> create(@RequestBody @Valid GuestInviteRequest request) {
        guestService.createGuest(request.getName(), request.getPhoneNumber(), request.getQuestionContent());
        return ResponseEntity.created(URI.create("this.is.temp.code")).build();  //TODO 추후 정상 반환이 생기면 수정
    }
}
