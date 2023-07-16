package com.ssh.dartserver.user.service;

import com.ssh.dartserver.university.domain.University;
import com.ssh.dartserver.university.infra.mapper.UniversityMapper;
import com.ssh.dartserver.university.infra.persistence.UniversityRepository;
import com.ssh.dartserver.user.domain.personalinfo.*;
import com.ssh.dartserver.user.dto.UserNextVoteResponse;
import com.ssh.dartserver.user.domain.User;
import com.ssh.dartserver.user.domain.recommendcode.RandomRecommendCodeGenerator;
import com.ssh.dartserver.user.dto.UserRequest;
import com.ssh.dartserver.user.dto.UserWithUniversityResponse;
import com.ssh.dartserver.user.infra.mapper.UserMapper;
import com.ssh.dartserver.user.infra.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor

public class UserService {
    private static final int NEXT_VOTE_AVAILABLE_MINUTES = 40;

    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final UserMapper userMapper;
    private final UniversityMapper universityMapper;
    private final RandomRecommendCodeGenerator randomGenerator;

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
    @Transactional
    public UserNextVoteResponse updateUserNextVoteAvailableDateTime(User user) {
        user.getNextVoteAvailableDateTime().plusMinutes(NEXT_VOTE_AVAILABLE_MINUTES);
        userRepository.save(user);
        return userMapper.toUserNextVoteResponseDto(user);
    }

    @Transactional
    public void delete(User user) {
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
