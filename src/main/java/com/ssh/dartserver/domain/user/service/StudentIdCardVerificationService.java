package com.ssh.dartserver.domain.user.service;

import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.university.dto.mapper.UniversityMapper;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.studentverificationinfo.StudentIdCardVerificationStatus;
import com.ssh.dartserver.domain.user.dto.UserStudentIdCardVerificationRequest;
import com.ssh.dartserver.domain.user.dto.UserWithUniversityResponse;
import com.ssh.dartserver.domain.user.dto.mapper.UserMapper;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class StudentIdCardVerificationService {

    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final UserMapper userMapper;
    private final UniversityMapper universityMapper;

    public UserWithUniversityResponse updateStudentIdCardVerificationStatus(User user, UserStudentIdCardVerificationRequest request) {
        user.getStudentVerificationInfo().updateStudentIdCardVerificationStatus(StudentIdCardVerificationStatus.VERIFICATION_IN_PROGRESS);
        user.getStudentVerificationInfo().updateStudentIdCardImageUrl(request.getStudentIdCardImageUrl());
        University university = universityRepository.findById(user.getUniversity().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대학입니다."));
        userRepository.save(user);
        //TODO: Slack webhook API 요청
        return userMapper.toUserWithUniversityResponse(userMapper.toUserResponse(user),
                universityMapper.toUniversityResponse(university));
    }
}
