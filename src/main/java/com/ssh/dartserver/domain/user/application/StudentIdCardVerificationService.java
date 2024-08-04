package com.ssh.dartserver.domain.user.application;

import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.university.application.UniversityMapper;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.studentverificationinfo.StudentIdCardVerificationStatus;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.domain.user.infra.VerifyMessageSender;
import com.ssh.dartserver.domain.user.presentation.v1.request.UserStudentIdCardVerificationRequest;
import com.ssh.dartserver.domain.user.presentation.v1.response.UserWithUniversityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class StudentIdCardVerificationService {

    private final UserRepository userRepository;
    private final UniversityRepository universityRepository;
    private final VerifyMessageSender verifyMessageSender;
    private final UserMapper userMapper;
    private final UniversityMapper universityMapper;

    public UserWithUniversityResponse updateStudentIdCardVerificationStatus(User user, UserStudentIdCardVerificationRequest request) {
        user.getStudentVerificationInfo().updateStudentIdCardVerificationStatus(StudentIdCardVerificationStatus.VERIFICATION_IN_PROGRESS);
        user.getStudentVerificationInfo().updateStudentIdCardImageUrl(request.getStudentIdCardImageUrl());

        verifyMessageSender.sendIdCardVerification(user.getId(), request.getName(), request.getStudentIdCardImageUrl());
        return newUserWithUniversityResponse(user);
    }

    public UserWithUniversityResponse updateStudentIdCardVerificationStatus(User user, StudentIdCardVerificationStatus status) {
        user.getStudentVerificationInfo().updateStudentIdCardVerificationStatus(status);
        return newUserWithUniversityResponse(user);
    }

    private UserWithUniversityResponse newUserWithUniversityResponse(User user) {
        University university = universityRepository.findById(user.getUniversity().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대학입니다."));
        userRepository.save(user);

        return userMapper.toUserWithUniversityResponse(
                userMapper.toUserResponse(user),
                universityMapper.toUniversityResponse(university));
    }
}
