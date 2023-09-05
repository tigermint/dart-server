package com.ssh.dartserver.domain.user.domain.studentverificationinfo;

public enum StudentIdCardVerificationStatus {
    //인증 전, 인증 중, 인증 성공, 인증 실패
    NOT_VERIFIED_YET, VERIFICATION_IN_PROGRESS, VERIFICATION_SUCCESS, VERIFICATION_FAILED;

    public boolean isNotVerifiedYet() {
        return this == NOT_VERIFIED_YET;
    }

    public boolean isVerificationInProgress() {
        return this == VERIFICATION_IN_PROGRESS;
    }

    public boolean isVerificationSuccess() {
        return this == VERIFICATION_SUCCESS;
    }

    public boolean isVerificationFailed() {
       return this == VERIFICATION_FAILED;
    }
}
