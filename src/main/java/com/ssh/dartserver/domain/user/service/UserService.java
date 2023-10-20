package com.ssh.dartserver.domain.user.service;

import com.ssh.dartserver.domain.friend.infra.FriendRepository;
import com.ssh.dartserver.domain.question.domain.Question;
import com.ssh.dartserver.domain.question.dto.mapper.QuestionMapper;
import com.ssh.dartserver.domain.team.domain.TeamUser;
import com.ssh.dartserver.domain.team.infra.TeamUserRepository;
import com.ssh.dartserver.domain.team.service.MyTeamService;
import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.university.dto.mapper.UniversityMapper;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.user.domain.Point;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.*;
import com.ssh.dartserver.domain.user.domain.profilequestions.ProfileQuestion;
import com.ssh.dartserver.domain.user.domain.recommendcode.RandomRecommendCodeGenerator;
import com.ssh.dartserver.domain.user.dto.UserProfileResponse;
import com.ssh.dartserver.domain.user.dto.UserSignupRequest;
import com.ssh.dartserver.domain.user.dto.UserUpdateRequest;
import com.ssh.dartserver.domain.user.dto.mapper.ProfileQuestionMapper;
import com.ssh.dartserver.domain.user.dto.mapper.UserMapper;
import com.ssh.dartserver.domain.user.infra.ProfileQuestionRepository;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.domain.vote.domain.Vote;
import com.ssh.dartserver.domain.vote.infra.CandidateRepository;
import com.ssh.dartserver.domain.vote.infra.VoteRepository;
import com.ssh.dartserver.global.util.RandomNicknameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private static final String DEFAULT_PROFILE_IMAGE_URL = "DEFAULT";
    private static final int DEFAULT_POINT = 0;

    private final RandomRecommendCodeGenerator randomCodeGenerator;

    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final FriendRepository friendRepository;
    private final VoteRepository voteRepository;
    private final ProfileQuestionRepository profileQuestionRepository;
    private final CandidateRepository candidateRepository;
    private final TeamUserRepository teamUserRepository;

    private final MyTeamService myTeamService;

    private final UserMapper userMapper;
    private final UniversityMapper universityMapper;
    private final ProfileQuestionMapper profileQuestionMapper;
    private final QuestionMapper questionMapper;

    @Transactional
    public Long signup(User user, UserSignupRequest userSignupRequest) {
        user.signup(
                getPersonalInfo(userSignupRequest),
                getUniversity(userSignupRequest.getUniversityId()),
                randomCodeGenerator,
                Point.from(DEFAULT_POINT)
        );
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }


    public UserProfileResponse read(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        List<ProfileQuestion> profileQuestions = profileQuestionRepository.findAllByUser(user);

        return getUserProfileResponse(user, profileQuestions);
    }

    @Transactional
    public UserProfileResponse update(User user, UserUpdateRequest request) {

        user.updateNickname(request.getNickname());
        user.updateProfileImageUrl(request.getProfileImageUrl());

        List<Question> receivedVoteQuestions = voteRepository.findAllByPickedUser(user).stream()
                .map(Vote::getQuestion)
                .collect(Collectors.toList());

        Map<Question, Long> receivedVoteQuestionCountMap = receivedVoteQuestions.stream()
                .collect(Collectors.groupingBy(question -> question, Collectors.counting()))
                .entrySet().stream()
                .filter(entry -> request.getProfileQuestionIds().contains(entry.getKey().getId()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        validateReceivedQuestion(request, receivedVoteQuestionCountMap);
        validateReceivedQuestionSize(request.getProfileQuestionIds());
        validateDuplicateQuestion(request.getProfileQuestionIds());

        List<ProfileQuestion> profileQuestions = receivedVoteQuestionCountMap.entrySet().stream()
                .map(entry -> ProfileQuestion.builder()
                        .question(entry.getKey())
                        .count(entry.getValue())
                        .user(user)
                        .build())
                .collect(Collectors.toList());

        profileQuestionRepository.deleteAllByUser(user);
        profileQuestionRepository.saveAll(profileQuestions);
        User savedUser = userRepository.save(user);

        return getUserProfileResponse(savedUser, profileQuestions);
    }

    @Transactional
    public void delete(User user) {
        friendRepository.deleteAllInBatchByUserOrFriendUser(user, user);

        List<Vote> pickedUserVotes = voteRepository.findAllByPickedUser(user);
        candidateRepository.deleteAllByVoteIn(pickedUserVotes);
        voteRepository.deleteAllByVoteIn(pickedUserVotes);

        voteRepository.updateAllPickingUserToNull(user);
        candidateRepository.updateAllUserToNull(user);

        teamUserRepository.findAllByUser(user).stream()
                .map(TeamUser::getTeam)
                .distinct()
                .collect(Collectors.toList())
                .forEach(team -> myTeamService.deleteTeam(user, team.getId()));

        userRepository.delete(user);
    }

    private UserProfileResponse getUserProfileResponse(User user, List<ProfileQuestion> profileQuestions) {
        return userMapper.toUserProfileResponse(
                userMapper.toUserResponse(user),
                universityMapper.toUniversityResponse(user.getUniversity()),
                profileQuestions.stream()
                        .map(profileQuestion ->
                                profileQuestionMapper.toProfileQuestionResponse(
                                        questionMapper.toQuestionResponse(profileQuestion.getQuestion()),
                                        profileQuestion.getCount()
                                ))
                        .collect(Collectors.toList())
        );
    }

    private PersonalInfo getPersonalInfo(UserSignupRequest request) {
        return PersonalInfo.builder()
                .phone(Phone.from(request.getPhone()))
                .name(Name.from(request.getName()))
                .nickname(Nickname.from(RandomNicknameGenerator.create()))
                .admissionYear(AdmissionYear.from(request.getAdmissionYear()))
                .birthYear(BirthYear.from(request.getBirthYear()))
                .gender(request.getGender())
                .profileImageUrl(ProfileImageUrl.from(DEFAULT_PROFILE_IMAGE_URL))
                .build();
    }

    private University getUniversity(Long universityId) {
        return universityRepository.findById(universityId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대학교입니다."));
    }

    private static void validateReceivedQuestion(UserUpdateRequest request, Map<Question, Long> receivedVoteQuestionCountMap) {
        if (receivedVoteQuestionCountMap.size() != request.getProfileQuestionIds().size()) {
            throw new IllegalArgumentException("내가 받은 프로필 질문이 아닙니다.");
        }
    }

    private void validateDuplicateQuestion(List<Long> profileQuestionIds) {
        if (profileQuestionIds.size() != profileQuestionIds.stream().distinct().count()) {
            throw new IllegalArgumentException("중복된 프로필 질문이 있습니다.");
        }
    }

    private void validateReceivedQuestionSize(List<Long> profileQuestionIds) {
        if (profileQuestionIds.size() > 3) {
            throw new IllegalArgumentException("프로필 질문은 3개 이하여야 합니다.");
        }
    }
}
