package com.ssh.dartserver.global.infra;

import com.ssh.dartserver.global.infra.notification.PlatformNotification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class MockPlatformNotification implements PlatformNotification {
    @Override
    public void postNotificationSpecificDevice(final List<Long> userIds, final String headings, final String contents) {
        System.out.println("User IDs: " + userIds);
        System.out.println("Headings: " + headings);
        System.out.println("Contents: " + contents);
    }

    @Override
    public void postNotificationSpecificDevice(final List<Long> userIds, final String contents) {
        System.out.println("User IDs: " + userIds);
        System.out.println("Contents: " + contents);
    }

    @Override
    public void postNotificationSpecificDevice(final Long userId, final String contents) {
        System.out.println("User ID: " + userId);
        System.out.println("Contents: " + contents);
    }

    @Override
    public void postNotificationNextVoteAvailableDateTime(final Long userId, final LocalDateTime localDateTime,
                                                          final String contents) {
        System.out.println("User ID: " + userId);
        System.out.println("Local Date Time: " + localDateTime);
        System.out.println("Contents: " + contents);
    }
}
