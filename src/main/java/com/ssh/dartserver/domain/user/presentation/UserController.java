package com.ssh.dartserver.domain.user.presentation;

import com.ssh.dartserver.domain.chat.dto.ChatRoomResponse;
import com.ssh.dartserver.domain.chat.service.ChatRoomService;
import com.ssh.dartserver.domain.proposal.dto.ProposalRequest;
import com.ssh.dartserver.domain.proposal.dto.ProposalResponse;
import com.ssh.dartserver.domain.proposal.service.ProposalService;
import com.ssh.dartserver.domain.question.dto.ReceivedQuestionResponse;
import com.ssh.dartserver.domain.question.service.QuestionService;
import com.ssh.dartserver.domain.team.dto.TeamRequest;
import com.ssh.dartserver.domain.team.dto.TeamResponse;
import com.ssh.dartserver.domain.team.service.MyTeamService;
import com.ssh.dartserver.domain.user.dto.*;
import com.ssh.dartserver.domain.user.service.NextVoteService;
import com.ssh.dartserver.domain.user.service.StudentIdCardVerificationService;
import com.ssh.dartserver.domain.user.service.UserService;
import com.ssh.dartserver.domain.vote.dto.ReceivedVoteDetailResponse;
import com.ssh.dartserver.domain.vote.dto.ReceivedVoteResponse;
import com.ssh.dartserver.domain.vote.service.VoteService;
import com.ssh.dartserver.global.auth.service.oauth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
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
    private final ProposalService proposalService;

    @PostMapping("/signup")
    public ResponseEntity<UserProfileResponse> signup(Authentication authentication, @Valid @RequestBody UserSignupRequest request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Long userId = userService.signup(principal.getUser(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userId)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> read(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(userService.read(principal.getUser().getId()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> update(Authentication authentication, @Valid @RequestBody UserUpdateRequest request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(userService.update(principal.getUser(), request));
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> delete(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        userService.delete(principal.getUser());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/next-voting-time")
    public ResponseEntity<UserNextVoteResponse> readNextVoteAvailableDateTime(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(nextVoteService.readNextVoteAvailableDateTime(principal.getUser()));
    }

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

    @Deprecated(since="20230901")
    @GetMapping("/me/teams")
    public ResponseEntity<List<TeamResponse>> listTeam(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(myTeamService.listTeam(principal.getUser()));
    }


    @Deprecated(since="20230901")
    @PatchMapping("/me/teams/{teamId}")
    public ResponseEntity<TeamResponse> updateTeam(Authentication authentication, @PathVariable Long teamId, @Valid @RequestBody TeamRequest request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(myTeamService.updateTeam(principal.getUser(), teamId, request));
    }

    @DeleteMapping("/me/teams/{teamId}")
    public ResponseEntity<String> deleteTeam(Authentication authentication, @PathVariable Long teamId) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        myTeamService.deleteTeam(principal.getUser(), teamId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/votes/{voteId}")
    public ResponseEntity<ReceivedVoteDetailResponse> readReceivedVote(Authentication authentication, @PathVariable Long voteId) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(voteService.readReceivedVote(principal.getUser(), voteId));
    }

    @GetMapping("/me/votes")
    public ResponseEntity<Page<ReceivedVoteResponse>> listReceivedVote(
            Authentication authentication,
            @RequestParam(required = false, defaultValue = "0", value = "page") int page,
            @RequestParam(required = false, defaultValue = "pickedTime", value = "criteria") String criteria) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(voteService.listReceivedVote(principal.getUser(), page, criteria));
    }

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

    @GetMapping("/me/proposals")
    public ResponseEntity<List<ProposalResponse.ListDto>> listProposal(Authentication authentication, @RequestParam(defaultValue = "sent") String type) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        if (type.equals("sent")) {  // TODO Enum
            return ResponseEntity.ok(proposalService.listSentProposal(principal.getUser()));
        }
        if (type.equals("received")) {
            return ResponseEntity.ok(proposalService.listReceivedProposal(principal.getUser()));
        }
        return ResponseEntity.badRequest().build();
    }

    @PatchMapping("/me/proposals/{proposalId}")
    public ResponseEntity<ProposalResponse.UpdateDto> updateProposal(Authentication authentication, @PathVariable Long proposalId, @RequestBody ProposalRequest.Update request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(proposalService.updateProposal(principal.getUser(), proposalId, request));
    }


}
