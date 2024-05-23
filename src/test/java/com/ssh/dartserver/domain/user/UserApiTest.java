package com.ssh.dartserver.domain.user;

import static com.ssh.dartserver.domain.university.UniversitySteps.*;
import static com.ssh.dartserver.domain.user.UserSteps.*;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.UserManager;
import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.domain.user.dto.UserSignupRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class UserApiTest extends ApiTest {
    @Autowired
    private UserManager userManager;
    @Autowired
    private UniversityRepository universityRepository;

    @Test
    void 회원가입_정보입력() {
        final List<University> universities = 대학생성요청_생성(10);
        universityRepository.saveAll(universities);
        final String jwtToken = userManager.createTestUser().getJwtToken();
        final UserSignupRequest request = 회원가입요청_생성();

        final ExtractableResponse<Response> response = 회원가입요청(jwtToken, request);

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
