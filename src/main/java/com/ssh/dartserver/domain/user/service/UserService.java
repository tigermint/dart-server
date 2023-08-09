package com.ssh.dartserver.domain.user.service;

import com.ssh.dartserver.domain.friend.infra.FriendRepository;
import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.university.dto.mapper.UniversityMapper;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.*;
import com.ssh.dartserver.domain.user.domain.recommendcode.RandomRecommendCodeGenerator;
import com.ssh.dartserver.domain.user.dto.UserSignupRequest;
import com.ssh.dartserver.domain.user.dto.UserUpdateRequest;
import com.ssh.dartserver.domain.user.dto.UserWithUniversityResponse;
import com.ssh.dartserver.domain.user.dto.mapper.UserMapper;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.domain.vote.infra.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private static final String DEFAULT_PROFILE_IMAGE_URL = "DEFAULT";
    private static final String DEFAULT_NICKNAME = "DEFAULT";

    private final RandomRecommendCodeGenerator randomGenerator;

    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final FriendRepository friendRepository;
    private final VoteRepository voteRepository;

    private final UserMapper userMapper;
    private final UniversityMapper universityMapper;

    @Transactional
    public UserWithUniversityResponse signup(User user, UserSignupRequest request) {
        user.signup(getPersonalInfo(request), getUniversity(request.getUniversityId()), randomGenerator);
        userRepository.save(user);
        return userMapper.toUserWithUniversityResponseDto(userMapper.toUserResponseDto(user),
                universityMapper.toUniversityResponseDto(user.getUniversity()));
    }

    public UserWithUniversityResponse read(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return userMapper.toUserWithUniversityResponseDto(userMapper.toUserResponseDto(user),
                universityMapper.toUniversityResponseDto(user.getUniversity()));
    }
    @Transactional
    public UserWithUniversityResponse update(User user, UserUpdateRequest request){
        user.updateNickname(request.getNickname());
        user.updateProfileImageUrl(request.getProfileImageUrl());
        University university = universityRepository.findById(user.getUniversity().getId())
                .orElse(null);
        return userMapper.toUserWithUniversityResponseDto(userMapper.toUserResponseDto(user),
                universityMapper.toUniversityResponseDto(university));
    }

    @Transactional
    public void delete(User user) {
        friendRepository.deleteAllByUserIdOrFriendUserId(user.getId(), user.getId());
        voteRepository.deleteAllByPickedUserId(user.getId());
        voteRepository.findAllByUserId(user.getId())
                .forEach(vote -> vote.updateUser(null));
        userRepository.delete(user);
    }

    private PersonalInfo getPersonalInfo(UserSignupRequest request) {
        return PersonalInfo.builder()
                .phone(Phone.from(request.getPhone()))
                .name(Name.from(request.getName()))
                .nickname(Nickname.from(DEFAULT_NICKNAME))
                .admissionYear(AdmissionYear.from(request.getAdmissionYear()))
                .birthYear(BirthYear.from(request.getBirthYear()))
                .gender(request.getGender())
                .profileImageUrl(ProfileImageUrl.from(DEFAULT_PROFILE_IMAGE_URL))
                .build();
    }

    private University getUniversity(Long universityId){
        return universityRepository.findById(universityId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대학교입니다."));
    }
}
