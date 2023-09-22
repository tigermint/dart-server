package com.ssh.dartserver.global.infra.notification;

import java.time.LocalDateTime;
import java.util.List;

public interface PlatformNotification {
    void postNotificationSpecificDevice(List<Long> userIds, String headings, String contents);
    void postNotificationSpecificDevice(List<Long> userIds, String contents);
    void postNotificationSpecificDevice(Long userId, String contents);

    void postNotificationNextVoteAvailableDateTime(Long userId, LocalDateTime localDateTime, String contents);
}
