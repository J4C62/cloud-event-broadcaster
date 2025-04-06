package dev.github.j4c62.broadcaster.core.service;

import dev.github.j4c62.broadcaster.core.composer.NotificationComposer;
import dev.github.j4c62.broadcaster.core.dto.CloudEvent;
import dev.github.j4c62.broadcaster.core.repository.DelivererSelector;

public class Broadcaster {
    private final DelivererSelector deliverers;
    private final NotificationComposer composer;

    public Broadcaster(DelivererSelector deliverers, NotificationComposer composer) {
        this.deliverers = deliverers;
        this.composer = composer;
    }

    public void broadcast(CloudEvent cloudEvent) {
        var delivers = deliverers.findDelivers(cloudEvent);

        var notifications = composer.compose();

        var filteredNotifications = notifications.stream()
                .filter(notification -> delivers.stream()
                        .anyMatch(deliverer -> deliverer.channel().name().equals(notification.channel().name())))
                .toList();

        filteredNotifications.forEach(notification ->
                delivers.stream()
                        .filter(deliverer -> deliverer.channel().name().equals(notification.channel().name()))
                        .forEach(deliverer -> deliverer.deliver(notification))
        );

    }


}
