package com.ssh.dartserver.domain.university;

import static com.ssh.dartserver.domain.university.UniversitySteps.*;
import static org.assertj.core.api.Assertions.*;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.UserManager;
import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.university.dto.UniversityResponse;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.testing.IntegrationTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("대학명으로 검색한다")
    void 대학목록확인() {
        int 생성대학수 = 100;
        List<University> universities = 대학생성요청_생성(생성대학수);
        universityRepository.saveAll(universities);
        final String jwtToken = userManager.createTestUser().getJwtToken();

        final ExtractableResponse<Response> response = 대학목록조회요청(jwtToken, 대학목록조회요청_생성());
        final UniversityResponse[] universityDtos = response.body().as(UniversityResponse[].class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(universityDtos).hasSize(10);
        assertThat(universityDtos[0].getName()).startsWith("Tech University ");
    }

    @Test
    @DisplayName("대학명 학과명으로 검색한다")
    void 대학이름으로검색() {
        int 생성대학수 = 100;
        List<University> universities = 대학생성요청_생성(생성대학수);
        universityRepository.saveAll(universities);
        final String jwtToken = userManager.createTestUser().getJwtToken();

        final ExtractableResponse<Response> response = 대학학과조회요청(jwtToken, 대학학과조회요청_생성());
        final UniversityResponse[] universityDtos = response.body().as(UniversityResponse[].class);

        assertThat(universityDtos).hasSize(2);
    }
}
