package dev.github.j4c62.broadcaster.core.deliverer;

import dev.github.j4c62.broadcaster.core.data.Channel;
import dev.github.j4c62.broadcaster.core.dto.Notification;

public interface Deliverer {
    void deliver(Notification notification);

    Channel channel();
}
