package com.ssh.dartserver.global.infra.notification;

import com.ssh.dartserver.global.config.properties.OneSignalProperty;
import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class OneSignalNotification implements PlatformNotification {
    private static final String ONESIGNAL_API_URL = "https://onesignal.com/api/v1/notifications";
    private static final String ONESIGNAL_APP_ID = "app_id";
    private static final String ONESIGNAL_INCLUDE_EXTERNAL_USER_IDS = "include_external_user_ids";
    private static final String ONESIGNAL_HEADINGS = "headings";
    private static final String ONESIGNAL_CONTENTS = "contents";
    private static final String ONESIGNAL_SEND_AFTER = "send_after";

    private final RestTemplate restTemplate;
    private final OneSignalProperty oneSignalProperty;

    @Override
    public void postNotificationSpecificDevice(List<Long> userIds, String headings, String contents) {
        HttpHeaders headers = getHttpHeaders();

        Map<String, Object> requestBody = Map.of(
                ONESIGNAL_APP_ID, oneSignalProperty.getAppId(),
                ONESIGNAL_INCLUDE_EXTERNAL_USER_IDS, userIds.stream().map(String::valueOf).collect(Collectors.toList()),
                ONESIGNAL_HEADINGS, Map.of("en", headings),
                ONESIGNAL_CONTENTS, Map.of("en", contents)
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        restTemplate.exchange(
                ONESIGNAL_API_URL,
                HttpMethod.POST,
                entity,
                String.class);
    }

    @Override
    public void postNotificationSpecificDevice(List<Long> userIds, String contents) {
        HttpHeaders headers = getHttpHeaders();

        Map<String, Object> requestBody = Map.of(
                ONESIGNAL_APP_ID, oneSignalProperty.getAppId(),
                ONESIGNAL_INCLUDE_EXTERNAL_USER_IDS, userIds.stream().map(String::valueOf).collect(Collectors.toList()),
                ONESIGNAL_CONTENTS, Map.of("en", contents)
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        restTemplate.exchange(
                ONESIGNAL_API_URL,
                HttpMethod.POST,
                entity,
                String.class);
    }

    @Override
    public void postNotificationSpecificDevice(Long userId, String contents) {
        HttpHeaders headers = getHttpHeaders();

        Map<String, Object> requestBody = Map.of(
                ONESIGNAL_APP_ID, oneSignalProperty.getAppId(),
                ONESIGNAL_INCLUDE_EXTERNAL_USER_IDS, List.of(String.valueOf(userId)),
                ONESIGNAL_CONTENTS, Map.of("en", contents)
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        restTemplate.exchange(
                ONESIGNAL_API_URL,
                HttpMethod.POST,
                entity,
                String.class);
    }


    @Override
    public void postNotificationNextVoteAvailableDateTime(Long userId, LocalDateTime dateTime, String contents) {
        HttpHeaders headers = getHttpHeaders();

        Map<String, Object> requestBody = Map.of(
                ONESIGNAL_APP_ID, oneSignalProperty.getAppId(),
                ONESIGNAL_SEND_AFTER, dateTime.toString(),
                ONESIGNAL_INCLUDE_EXTERNAL_USER_IDS, List.of(String.valueOf(userId)),
                ONESIGNAL_CONTENTS, Map.of("en", contents)
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        restTemplate.exchange(
                ONESIGNAL_API_URL,
                HttpMethod.POST,
                entity,
                String.class);
    }


    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + oneSignalProperty.getRestApiKey());
        return headers;
    }
}
