package com.ssh.dartserver.domain.health;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.testing.IntegrationTest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@IntegrationTest
public class HealthApiTest extends ApiTest {
    @Test
    void 헬스체크() {
        final ExtractableResponse<Response> response = 헬스체크요청();

        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static ExtractableResponse<Response> 헬스체크요청() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/v1/health")
            .then()
            .log().all().extract();
    }
}
