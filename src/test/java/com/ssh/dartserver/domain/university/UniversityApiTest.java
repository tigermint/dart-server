package com.ssh.dartserver.domain.university;

import static com.ssh.dartserver.domain.university.UniversitySteps.*;
import static org.assertj.core.api.Assertions.*;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.UserManager;
import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.university.presentation.response.UniversityResponse;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.testing.IntegrationTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@IntegrationTest
public class UniversityApiTest extends ApiTest {
    @Autowired
    private UniversityRepository universityRepository;
    @Autowired
    private UserManager userManager;

    @Test
    void 대학목록확인() {
        long 생성대학수 = 100L;
        List<University> universities = 대학생성요청_생성(생성대학수);
        universityRepository.saveAll(universities);
        final String jwtToken = userManager.createTestUser().getJwtToken();

        final ExtractableResponse<Response> response = 대학목록조회요청(jwtToken);
        final UniversityResponse[] universityDtos = response.body().as(UniversityResponse[].class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(universityDtos.length).isEqualTo(생성대학수);
    }

}
