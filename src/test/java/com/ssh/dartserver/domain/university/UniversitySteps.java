package com.ssh.dartserver.domain.university;

import com.ssh.dartserver.domain.university.domain.University;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.MediaType;

public class UniversitySteps {
    public static List<University> 대학생성요청_생성(long count) {
        List<University> universities = new ArrayList<>();
        for (long i=0; i<=count; i++) {
            University university = University.builder()
                .id(i)
                .area("Science")
                .name("Tech University " + i)
                .type("Urban")
                .department("Engineering")
                .state("California")
                .div0("Division0")
                .div1("Division1")
                .div2("Division2")
                .div3("Division3")
                .years("Four")
                .build();

            universities.add(university);
        }
        return universities;
    }

    public static ExtractableResponse<Response> 대학목록조회요청(final String jwtToken) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .auth().oauth2(jwtToken)
            .when()
            .get("/v1/universities")
            .then()
            .log().all().extract();
    }
}
