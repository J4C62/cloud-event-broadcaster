package dev.github.j4c62.broadcaster.core.dto;

import dev.github.j4c62.broadcaster.core.data.Channel;

import static dev.github.j4c62.broadcaster.core.data.Channel.EMAIL;

public record Email(String from, String to, String subject, String body) implements Notification {
    @Override
    public Channel channel() {
        return EMAIL;
    }
}
