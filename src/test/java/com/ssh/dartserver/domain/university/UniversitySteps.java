package com.ssh.dartserver.domain.university;

import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.university.dto.UniversitySearchRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.MediaType;

public class UniversitySteps {
    public static List<University> 대학생성요청_생성(long count) {
        List<University> universities = new ArrayList<>();
        for (long i = 0; i <= count; i++) {
            University university = University.builder()
                .id(i)
                .area("Science")
                .name("Tech University " + i)
                .type("Urban")
                .department("Engineering")
                .state("California")
                .build();

            universities.add(university);
        }
        return universities;
    }

    public static ExtractableResponse<Response> 대학목록조회요청(final String jwtToken, UniversitySearchRequest request) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .auth().oauth2(jwtToken)
            .when()
            .get("/v1/universities?name={name}&size={size}",
                request.getName(),
                request.getSize())
            .then()
            .log().all().extract();
    }

    public static UniversitySearchRequest 대학목록조회요청_생성() {
        final UniversitySearchRequest request = new UniversitySearchRequest();
        request.setName("Tech University 1");
        return request;
    }
}
