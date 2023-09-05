package com.ssh.dartserver.domain.user.domain.studentverificationinfo;

import lombok.Getter;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Embeddable
public class StudentVerificationInfo {
    @Enumerated(EnumType.STRING)
    private StudentIdCardVerificationStatus studentIdCardVerificationStatus;
    @Embedded
    private StudentIdCardImageUrl studentIdCardImageUrl;

    public StudentVerificationInfo() {
        this.studentIdCardVerificationStatus = StudentIdCardVerificationStatus.NOT_VERIFIED_YET;
        this.studentIdCardImageUrl = new StudentIdCardImageUrl();
    }

    public static StudentVerificationInfo newInstance() {
        return new StudentVerificationInfo();
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

