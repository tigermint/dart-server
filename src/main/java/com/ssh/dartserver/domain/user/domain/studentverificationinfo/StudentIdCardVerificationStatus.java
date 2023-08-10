package com.ssh.dartserver.domain.user.domain.studentverificationinfo;

public enum StudentIdCardVerificationStatus {
    //인증 전, 인증 중, 인증 성공, 인증 실패
    NOT_VERIFIED_YET, VERIFICATION_IN_PROGRESS, VERIFICATION_SUCCESS, VERIFICATION_FAILED;
}
