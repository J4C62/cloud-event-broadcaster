package dev.github.j4c62.broadcaster.core.dto;

import dev.github.j4c62.broadcaster.core.data.Channel;

import static dev.github.j4c62.broadcaster.core.data.Channel.EVENT_BRIDGE;

public record EventBridge(String source, String resources, String detailType, String detail) implements Notification {
    @Override
    public Channel channel() {
        return EVENT_BRIDGE;
    }
}
