package dev.github.j4c62.broadcaster.core.dto;

import com.fasterxml.jackson.databind.JsonNode;
import dev.github.j4c62.broadcaster.core.data.Channel;

import java.net.URI;

import static dev.github.j4c62.broadcaster.core.data.Channel.EVENT_BRIDGE;

public record CloudEvent(
        String id,
        URI source,
        String type,
        String time,
        JsonNode data
) implements Notification {

    @Override
    public Channel channel() {
        return EVENT_BRIDGE;
    }
}