package com.ssh.dartserver.global.infra.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class OneSignalNotification implements PlatformNotification{
    private final RestTemplate restTemplate;

    //환경변수 처리 수정 필요
    @Value("${onesignal.rest-api-key}")
    private String oneSignalRestApiKey;

    @Value("${onesignal.app-id}")
    private String oneSignalAppId;
    @Override
    public void postNotificationSpecificDevice(Long userId, String contents){
        HttpHeaders headers = getHttpHeaders();

        Map<String, Object> requestBody = Map.of(
                "app_id", oneSignalAppId,
                "include_external_user_ids", List.of(String.valueOf(userId)),
                "contents", Map.of("en", contents)
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        restTemplate.exchange(
                "https://onesignal.com/api/v1/notifications",
                HttpMethod.POST,
                entity,
                String.class);
    }

    @Override
    public void postNotificationNextVoteAvailableDateTime(Long userId, LocalDateTime dateTime, String contents) {
        HttpHeaders headers = getHttpHeaders();

        Map<String, Object> requestBody = Map.of(
                "app_id", oneSignalAppId,
                "send_after", dateTime.toString(),
                "include_external_user_ids", List.of(String.valueOf(userId)),
                "contents", Map.of("en", contents)
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        restTemplate.exchange(
                "https://onesignal.com/api/v1/notifications",
                HttpMethod.POST,
                entity,
                String.class);
    }


    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + oneSignalRestApiKey);
        return headers;
    }
}
