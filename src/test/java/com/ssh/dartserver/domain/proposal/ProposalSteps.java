package com.ssh.dartserver.domain.proposal;

import com.ssh.dartserver.domain.proposal.presentation.request.ProposalRequest;
import com.ssh.dartserver.domain.proposal.presentation.request.ProposalRequest.Create;
import com.ssh.dartserver.domain.proposal.presentation.request.ProposalRequest.Update;
import com.ssh.dartserver.domain.team.presentation.request.TeamRequest;
import com.ssh.dartserver.domain.team.presentation.request.TeamRequest.SingleTeamFriendDto;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import org.springframework.http.MediaType;

public class ProposalSteps {
    public static ExtractableResponse<Response> 호감보내기요청(String jwtToken, Create request) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .auth().oauth2(jwtToken)
            .body(request)
            .when()
            .post("/v1/proposals")
            .then()
            .log().all().extract();
    }

    public static ProposalRequest.Create 호감보내기요청_생성(long requestingTeam, long requestedTeam) {
        ProposalRequest.Create request = new Create();
        request.setRequestingTeamId(requestingTeam);
        request.setRequestedTeamId(requestedTeam);

        return request;
    }

    public static ExtractableResponse<Response> 보낸호감조회요청(String jwtToken) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .auth().oauth2(jwtToken)
            .when()
            .get("/v1/users/me/proposals")
            .then()
            .log().all().extract();
    }

    public static ExtractableResponse<Response> 받은호감조회요청(String jwtToken) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .auth().oauth2(jwtToken)
            .when()
            .get("/v1/users/me/proposals?type=received")
            .then()
            .log().all().extract();
    }

    public static ExtractableResponse<Response> 호감수락거절요청(String jwtToken, long proposalId, ProposalRequest.Update request) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .auth().oauth2(jwtToken)
            .body(request)
            .when()
            .patch("/v1/users/me/proposals/" + proposalId)
            .then()
            .log().all().extract();
    }

    public static ProposalRequest.Update 호감수락요청_생성() {
        final Update update = new Update();
        update.setProposalStatus("PROPOSAL_SUCCESS");

        return update;
    }

    public static ProposalRequest.Update 호감거절요청_생성() {
        final Update update = new Update();
        update.setProposalStatus("PROPOSAL_FAILED");

        return update;
    }

    public static SingleTeamFriendDto 가입하지않은팀원_생성() {
        final SingleTeamFriendDto singleTeamFriendDto = new SingleTeamFriendDto();
        singleTeamFriendDto.setNickname("닉네임");
        singleTeamFriendDto.setBirthYear(2005);
        singleTeamFriendDto.setUniversityId(1L);

        return singleTeamFriendDto;
    }

    public static TeamRequest 팀생성요청_생성(List<Long> regionIds, List<Long> userIds, List<SingleTeamFriendDto> friends) {
        final TeamRequest request = new TeamRequest();
        request.setName("팀이름" + (int) Math.random() % 1 * 1000);
        request.setIsVisibleToSameUniversity(false);
        request.setRegionIds(regionIds);
        request.setUserIds(userIds);
        request.setSingleTeamFriends(friends);

        return request;
    }

    public static String getCreatedProposalId(final long requestingTeamId, final long requestedTeamId, final String requestingTeamUserJwtToken) {
        final ProposalRequest.Create proposalRequest = ProposalSteps.호감보내기요청_생성(requestingTeamId, requestedTeamId);
        final ExtractableResponse<Response> extractableResponse = ProposalSteps.호감보내기요청(requestingTeamUserJwtToken, proposalRequest);
        final String location = extractableResponse.header("Location");
        return location.substring(location.lastIndexOf("/") + 1);
    }
}
