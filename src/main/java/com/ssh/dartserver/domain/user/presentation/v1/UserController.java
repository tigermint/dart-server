package com.ssh.dartserver.domain.user.presentation.v1;

import com.ssh.dartserver.domain.chat.presentation.response.ChatRoomResponse;
import com.ssh.dartserver.domain.chat.application.ChatRoomService;
import com.ssh.dartserver.domain.question.presentation.response.ReceivedQuestionResponse;
import com.ssh.dartserver.domain.question.application.QuestionService;
import com.ssh.dartserver.domain.team.presentation.request.TeamRequest;
import com.ssh.dartserver.domain.team.presentation.response.TeamResponse;
import com.ssh.dartserver.domain.team.application.MyTeamService;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.presentation.v1.request.UserSignUpRequest;
import com.ssh.dartserver.domain.user.presentation.v1.request.UserStudentIdCardVerificationRequest;
import com.ssh.dartserver.domain.user.presentation.v1.request.UserUpdateRequest;
import com.ssh.dartserver.domain.user.presentation.v1.response.UserNextVoteResponse;
import com.ssh.dartserver.domain.user.presentation.v1.response.UserProfileResponse;
import com.ssh.dartserver.domain.user.presentation.v1.response.UserWithUniversityResponse;
import com.ssh.dartserver.domain.user.application.NextVoteService;
import com.ssh.dartserver.domain.user.application.StudentIdCardVerificationService;
import com.ssh.dartserver.domain.user.application.UserService;
import com.ssh.dartserver.domain.vote.presentation.response.ReceivedVoteDetailResponse;
import com.ssh.dartserver.domain.vote.presentation.response.ReceivedVoteResponse;
import com.ssh.dartserver.domain.vote.application.VoteService;
import com.ssh.dartserver.global.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController("userControllerV1")
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {
    private final UserService userService;
    private final VoteService voteService;
    private final NextVoteService nextVoteService;
    private final StudentIdCardVerificationService studentIdCardVerificationService;
    private final MyTeamService myTeamService;
    private final QuestionService questionService;
    private final ChatRoomService chatRoomService;

    @PostMapping("/signup")
    public ResponseEntity<UserProfileResponse> signUp(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody UserSignUpRequest request
    ) {
        final User user = userService.signUp(principalDetails.getUser(), request.toPersonalInfo(), request.getUniversityId());
        return ResponseEntity.created(URI.create("/v1/users/signup/" + user.getId())).build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> read(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        final User user = userService.read(principalDetails.getUser().getId());
        return ResponseEntity.ok(UserProfileResponse.of(user));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> update(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        final User user = userService.update(principalDetails.getUser(), request.getNickname(), request.getProfileImageUrl());
        return ResponseEntity.ok(UserProfileResponse.of(user));
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> delete(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        userService.delete(principalDetails.getUser());
        return ResponseEntity.noContent().build();
    }

    @Deprecated(since="20240724", forRemoval = true)
    @GetMapping("/me/next-voting-time")
    public ResponseEntity<UserNextVoteResponse> readNextVoteAvailableDateTime(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(nextVoteService.readNextVoteAvailableDateTime(principal.getUser()));
    }


    @Deprecated(since="20240724", forRemoval = true)
    @PostMapping("/me/next-voting-time")
    public ResponseEntity<UserNextVoteResponse> updateNextVoteAvailableDateTime(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(nextVoteService.updateNextVoteAvailableDateTime(principal.getUser()));
    }

    @PostMapping("/me/verify-student-id-card")
    public ResponseEntity<UserWithUniversityResponse> updateStudentIdCardVerificationStatus(
            Authentication authentication, @Valid @RequestBody UserStudentIdCardVerificationRequest request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(studentIdCardVerificationService.updateStudentIdCardVerificationStatus(principal.getUser(), request));
    }

    @GetMapping("/me/teams/{teamId}")
    public ResponseEntity<TeamResponse> readTeam(Authentication authentication, @PathVariable Long teamId) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(myTeamService.readTeam(principal.getUser(), teamId));
    }

    @Deprecated(since = "20230901")
    @GetMapping("/me/teams")
    public ResponseEntity<List<TeamResponse>> listTeam(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(myTeamService.listTeam(principal.getUser()));
    }


    @Deprecated(since = "20230901")
    @PatchMapping("/me/teams/{teamId}")
    public ResponseEntity<TeamResponse> updateTeam(Authentication authentication, @PathVariable Long teamId, @Valid @RequestBody TeamRequest request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(myTeamService.updateTeam(principal.getUser(), teamId, request));
    }

    // v1 team delete logic (deprecated)
    @Deprecated(since = "20241017")
    @DeleteMapping("/me/teams/{teamId}")
    public ResponseEntity<String> deleteTeam(Authentication authentication, @PathVariable Long teamId) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        myTeamService.deleteTeam(principal.getUser(), teamId);
        return ResponseEntity.noContent().build();
    }

    @Deprecated(since="20240724", forRemoval = true)
    @GetMapping("/me/votes/{voteId}")
    public ResponseEntity<ReceivedVoteDetailResponse> readReceivedVote(Authentication authentication, @PathVariable Long voteId) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(voteService.readReceivedVote(principal.getUser(), voteId));
    }

    @Deprecated(since="20240724", forRemoval = true)
    @GetMapping("/me/votes")
    public ResponseEntity<Page<ReceivedVoteResponse>> listReceivedVote(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "0", value = "page") int page,
            @RequestParam(required = false, defaultValue = "pickedTime", value = "criteria") String criteria) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(voteService.listReceivedVote(principal.getUser(), page, criteria));
    }

    @Deprecated(since="20240724", forRemoval = true)
    @GetMapping("/me/questions")
    public ResponseEntity<List<ReceivedQuestionResponse>> listReceivedQuestion(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(questionService.listReceivedVoteQuestion(principal.getUser()));
    }

    @GetMapping("/me/chat/rooms/{chatRoomId}")
    public ResponseEntity<ChatRoomResponse.ReadDto> readChatRoom(@PathVariable Long chatRoomId, Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(chatRoomService.readChatRoom(chatRoomId, principal.getUser()));
    }

    @GetMapping("/me/chat/rooms")
    public ResponseEntity<List<ChatRoomResponse.ListDto>> listChatRoom(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(chatRoomService.listChatRoom(principal.getUser()));
    }

}
