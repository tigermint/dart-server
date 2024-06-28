package com.ssh.dartserver.domain.user;

import static com.ssh.dartserver.domain.university.UniversitySteps.대학생성요청_생성;
import static com.ssh.dartserver.domain.user.StudentVerifySteps.*;
import static com.ssh.dartserver.domain.user.UserSteps.*;
import static org.assertj.core.api.Assertions.*;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.UserManager;
import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.user.dto.UserStudentIdCardVerificationRequest;
import com.ssh.dartserver.testing.IntegrationTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@IntegrationTest
public abstract class StudentVerifyApiTest extends ApiTest {
    @Autowired
    private UserManager userManager;
    @Autowired
    private UniversityRepository universityRepository;

    // POST /v1/users/me/verify-student-id-card
    @Test
    void 학생증인증() {
        final String jwtToken = userManager.createTestUser().getJwtToken();
        대학생성(1);
        회원가입요청(jwtToken, 회원가입요청_생성());
        final UserStudentIdCardVerificationRequest request = 학생증인증요청_생성();

        final ExtractableResponse<Response> response = 학생증인증요청(jwtToken, request);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 대학생성(int count) {
        final List<University> universities = 대학생성요청_생성(count);
        universityRepository.saveAll(universities);
    }
}
