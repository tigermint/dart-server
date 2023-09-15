package com.ssh.dartserver.domain.admin.service;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.studentverificationinfo.StudentIdCardVerificationStatus;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.domain.user.service.StudentIdCardVerificationService;
import com.ssh.dartserver.global.infra.notification.PlatformNotification;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminIdCardService {
    private final StudentIdCardVerificationService studentIdCardVerificationService;
    private final UserRepository userRepository;
    private final PlatformNotification notification;

    public void verifyIdCard(Long userId, String sign) {
        User user = userRepository.findById(userId).orElseThrow();

        if (sign.equals("success")) {
            studentIdCardVerificationService.updateStudentIdCardVerificationStatus(user, StudentIdCardVerificationStatus.VERIFICATION_SUCCESS);
            successNotification(userId);
        }
        if (sign.equals("failed")) {
            studentIdCardVerificationService.updateStudentIdCardVerificationStatus(user, StudentIdCardVerificationStatus.VERIFICATION_FAILED);
            failedNotification(userId);
        }
    }

    private void successNotification(Long userId) {
        final String contents = "학생증 인증이 완료되었어요! 😆";
        CompletableFuture.runAsync(() -> notification.postNotificationSpecificDevice(userId, contents));
    }

    private void failedNotification(Long userId) {
        final String contents = "학생증 인증이 실패했어요... 😢";
        CompletableFuture.runAsync(() -> notification.postNotificationSpecificDevice(userId, contents));
    }
}
