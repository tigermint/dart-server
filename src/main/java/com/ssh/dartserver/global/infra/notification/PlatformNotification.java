package com.ssh.dartserver.global.infra.notification;

import java.time.LocalDateTime;

public interface PlatformNotification {
    void postNotificationSpecificDevice(Long userId, String contents);

    void postNotificationNextVoteAvailableDateTime(Long userId, LocalDateTime localDateTime, String contents);
}
