package com.ssh.dartserver.domain.user;

import static com.ssh.dartserver.domain.university.UniversitySteps.*;
import static com.ssh.dartserver.domain.user.UserSteps.*;
import static org.assertj.core.api.Assertions.*;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.UserManager;
import com.ssh.dartserver.domain.team.UsersTeamApiTest;
import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.user.dto.UserProfileResponse;
import com.ssh.dartserver.domain.user.dto.UserResponse;
import com.ssh.dartserver.domain.user.dto.UserSignupRequest;
import com.ssh.dartserver.domain.user.dto.UserUpdateRequest;
import com.ssh.dartserver.testing.IntegrationTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@IntegrationTest
@Nested
public class UserApiTest extends ApiTest {
    @Autowired
    private UserManager userManager;
    @Autowired
    private UniversityRepository universityRepository;

    @Nested
    class StudentVerifyTest extends StudentVerifyApiTest {
    }

    @Test
    void 회원가입_정보입력() {
        final String jwtToken = userManager.createTestUser().getJwtToken();
        대학생성(10);
        final UserSignupRequest request = 회원가입요청_생성();

        final ExtractableResponse<Response> response = 회원가입요청(jwtToken, request);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    // GET /v1/users/me
    @Test
    void 내정보_확인하기() {
        final String jwtToken = userManager.createTestUser().getJwtToken();
        대학생성(10);
        회원가입요청(jwtToken, 회원가입요청_생성());

        final ExtractableResponse<Response> response = 내정보확인요청(jwtToken);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final UserProfileResponse userProfileResponse = response.body().as(UserProfileResponse.class);
        assertThat(userProfileResponse.getUserResponse()).isNotNull();
        assertThat(userProfileResponse.getUniversityResponse()).isNotNull();
        assertThat(userProfileResponse.getUserResponse().getName()).isEqualTo("테스트");
    }

    // DELETE /v1/users/me
    @Test
    void 회원탈퇴하기() {
        final String jwtToken = userManager.createTestUser().getJwtToken();
        대학생성(10);
        회원가입요청(jwtToken, 회원가입요청_생성());

        final ExtractableResponse<Response> response = 회원탈퇴요청(jwtToken);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    // PATCH /v1/users/me
    @Test
    void 내정보_수정하기() {
        final String jwtToken = userManager.createTestUser().getJwtToken();
        대학생성(10);
        회원가입요청(jwtToken, 회원가입요청_생성());
        final UserUpdateRequest request = 내정보수정요청_생성();

        final ExtractableResponse<Response> response = 내정보수정요청(jwtToken, request);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final UserProfileResponse userProfileResponse = response.body().as(UserProfileResponse.class);
        assertThat(userProfileResponse.getUserResponse().getNickname()).isEqualTo("수정된닉네임");
    }

    private void 대학생성(int count) {
        final List<University> universities = 대학생성요청_생성(count);
        universityRepository.saveAll(universities);
    }
}
