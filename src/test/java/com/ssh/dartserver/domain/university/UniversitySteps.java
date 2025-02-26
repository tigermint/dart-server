package com.ssh.dartserver.domain.university;

import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.university.presentation.response.UniversitySearchRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UniversitySteps {
    public static List<University> 대학생성요청_생성(int count) {
        return IntStream.range(1000, 1000 + count)
            .mapToObj(j ->
                Arrays.stream(DepartmentForTest.values())
                    .map(department -> createNewUniversity(
                        j + department.ordinal(),
                        "Tech University " + j,
                        department.getKoreanName()))
                    .collect(Collectors.toList())
            )
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    }

    private static University createNewUniversity(final long i, final String name, final String department) {
        University university = University.builder()
            .id(i)
            .area("서울")
            .name(name)
            .department(department)
            .build();
        return university;
    }

    public static ExtractableResponse<Response> 대학목록조회요청(final String jwtToken, UniversitySearchRequest request) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .auth().oauth2(jwtToken)
            .when()
            .get("/v1/universities?name={name}", request.getName())
            .then()
            .log().all().extract();
    }

    public static ExtractableResponse<Response> 대학학과조회요청(final String jwtToken, UniversitySearchRequest request) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .auth().oauth2(jwtToken)
            .when()
            .get("/v1/universities?name={name}&department={department}", request.getName(), request.getDepartment())
            .then()
            .log().all().extract();
    }

    public static UniversitySearchRequest 대학목록조회요청_생성() {
        final UniversitySearchRequest request = new UniversitySearchRequest();
        request.setName("Tech University 1");
        return request;
    }

    public static UniversitySearchRequest 대학학과조회요청_생성() {
        final UniversitySearchRequest request = new UniversitySearchRequest();
        request.setName("Tech University 1000");
        request.setDepartment("컴퓨터");
        return request;
    }
}
