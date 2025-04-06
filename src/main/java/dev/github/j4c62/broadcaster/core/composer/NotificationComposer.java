package dev.github.j4c62.broadcaster.core.composer;

import dev.github.j4c62.broadcaster.core.dto.Notification;

import java.util.List;

public interface NotificationComposer {
    List<Notification> compose();
}
