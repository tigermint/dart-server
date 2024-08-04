package com.ssh.dartserver.domain.region;

import com.ssh.dartserver.domain.team.domain.Region;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;

public class RegionSteps {
    public static ExtractableResponse<Response> 지역목록확인요청(String jwtToken) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .auth().oauth2(jwtToken)
                .when()
                .get("/v1/regions")
                .then()
                .log().all().extract();
    }

    public static List<Region> 지역생성요청_생성(long count) {
        List<Region> regions = new ArrayList<>();
        for (long i = 0; i <= count; i++) {
            Region region = Region.builder()
                    .id(i)
                    .name("region" + i)
                    .build();

            regions.add(region);
        }

        return regions;
    }
}
