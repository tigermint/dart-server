package com.ssh.dartserver.domain.user.service;

import com.ssh.dartserver.domain.friend.infra.FriendRepository;
import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.university.dto.mapper.UniversityMapper;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.*;
import com.ssh.dartserver.domain.user.domain.recommendcode.RandomRecommendCodeGenerator;
import com.ssh.dartserver.domain.user.dto.UserNextVoteResponse;
import com.ssh.dartserver.domain.user.dto.UserRequest;
import com.ssh.dartserver.domain.user.dto.UserWithUniversityResponse;
import com.ssh.dartserver.domain.user.dto.mapper.UserMapper;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.domain.vote.infra.VoteRepository;
import com.ssh.dartserver.global.infra.notification.PlatformNotification;
import com.ssh.dartserver.global.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private static final int NEXT_VOTE_AVAILABLE_MINUTES = 40;
    private final RandomRecommendCodeGenerator randomGenerator;

    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final FriendRepository friendRepository;
    private final VoteRepository voteRepository;

    private final UserMapper userMapper;
    private final UniversityMapper universityMapper;

    private final PlatformNotification notification;


    public UserWithUniversityResponse read(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return userMapper.toUserWithUniversityResponseDto(userMapper.toUserResponseDto(user),
                universityMapper.toUniversityResponseDto(user.getUniversity()));
    }

    @Transactional
    public UserWithUniversityResponse completeSignupWithRecommendationCode(User user, UserRequest userRequestDto) {
        user.updateWithRecommendationCode(getPersonalInfo(userRequestDto), getUniversity(userRequestDto.getUniversityId()), randomGenerator);
        userRepository.save(user);
        return userMapper.toUserWithUniversityResponseDto(userMapper.toUserResponseDto(user),
                universityMapper.toUniversityResponseDto(user.getUniversity()));
    }


    @Transactional
    public UserWithUniversityResponse updateUserInformation(User user, UserRequest userRequestDto) {
        user.update(getPersonalInfo(userRequestDto), getUniversity(userRequestDto.getUniversityId()));
        userRepository.save(user);
        return userMapper.toUserWithUniversityResponseDto(userMapper.toUserResponseDto(user),
                universityMapper.toUniversityResponseDto(user.getUniversity()));
    }

    public UserNextVoteResponse readNextVoteAvailableDateTime(User user) {
        return userMapper.toUserNextVoteResponseDto(user.getNextVoteAvailableDateTime().getValue());
    }

    @Transactional
    public UserNextVoteResponse updateNextVoteAvailableDateTime(User user) {
        user.updateNextVoteAvailableDateTime(NEXT_VOTE_AVAILABLE_MINUTES);

        userRepository.save(user);

        //사용자의 다음 투표 가능 시간 예약
        String contents = "새로운 투표가 가능합니다. Dart로 돌아와주세요!";
        notification.postNotificationNextVoteAvailableDateTime(user.getId(), DateTimeUtils.toUTC(user.getNextVoteAvailableDateTime().getValue()), contents);
        return userMapper.toUserNextVoteResponseDto(user.getNextVoteAvailableDateTime().getValue());
    }

    @Transactional
    public void delete(User user) {
        friendRepository.deleteAllByUserIdOrFriendUserId(user.getId(), user.getId());
        voteRepository.deleteAllByPickedUserId(user.getId());
        voteRepository.findAllByUserId(user.getId())
                .forEach(vote -> vote.updateUser(null));
        userRepository.delete(user);
    }

    private PersonalInfo getPersonalInfo(UserRequest userRequestDto) {
        return PersonalInfo.builder()
                .phone(Phone.newInstance(userRequestDto.getPhone()))
                .name(Name.newInstance(userRequestDto.getName()))
                .admissionYear(AdmissionYear.newInstance(userRequestDto.getAdmissionYear()))
                .birthYear(BirthYear.newInstance(userRequestDto.getBirthYear()))
                .gender(userRequestDto.getGender())
                .build();
    }

    private University getUniversity(Long universityId){
        return universityRepository.findById(universityId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대학교입니다."));
    }
}
