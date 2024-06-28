package com.ssh.dartserver.domain.region;

import static com.ssh.dartserver.domain.region.RegionSteps.*;
import static org.assertj.core.api.Assertions.*;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.UserManager;
import com.ssh.dartserver.domain.team.domain.Region;
import com.ssh.dartserver.domain.team.infra.RegionRepository;
import com.ssh.dartserver.testing.IntegrationTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@IntegrationTest
public class RegionApiTest  extends ApiTest {
    @Autowired
    private UserManager userManager;
    @Autowired
    private RegionRepository regionRepository;

    // GET /v1/regions
    @Test
    void 지역목록확인() {
        final String jwtToken = userManager.createTestUser().getJwtToken();
        int regionsCount = 10;
        regionRepository.saveAll(지역생성요청_생성(regionsCount));

        final ExtractableResponse<Response> response = 지역목록확인요청(jwtToken);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().as(Region[].class)).hasSize(regionsCount);
    }
}
