package com.ssh.dartserver.domain.user.domain.studentverificationinfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudentVerificationInfo {

    @Enumerated(EnumType.STRING)
    private StudentIdCardVerificationStatus studentIdCardVerificationStatus;

    @Embedded
    private StudentIdCardImageUrl studentIdCardImageUrl;

    private StudentVerificationInfo(StudentIdCardVerificationStatus studentIdCardVerificationStatus, StudentIdCardImageUrl studentIdCardImageUrl) {
        this.studentIdCardVerificationStatus = studentIdCardVerificationStatus;
        this.studentIdCardImageUrl = studentIdCardImageUrl;
    }

    public static StudentVerificationInfo newInstance() {
        return new StudentVerificationInfo(
                StudentIdCardVerificationStatus.NOT_VERIFIED_YET,
                StudentIdCardImageUrl.newInstance()
        );
    }

    public void updateStudentIdCardVerificationStatus(StudentIdCardVerificationStatus status) {
        this.studentIdCardVerificationStatus = status;
    }

    public void updateStudentIdCardImageUrl(String value) {
        this.studentIdCardImageUrl = StudentIdCardImageUrl.from(value);
    }

    public boolean isCertified() {
        return this.studentIdCardVerificationStatus.isVerificationSuccess();
    }
}

