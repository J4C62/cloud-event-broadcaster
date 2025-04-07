package dev.github.j4c62.broadcaster.core.broadcaster;

import dev.github.j4c62.broadcaster.core.composer.DiffusibleComposer;
import dev.github.j4c62.broadcaster.core.selector.DelivererSelector;
import dev.github.j4c62.broadcaster.infra.delivery.dto.CloudEvent;
import java.util.Objects;

public class Broadcaster {
  private final DelivererSelector deliverers;
  private final DiffusibleComposer composer;

  public Broadcaster(DelivererSelector deliverers, DiffusibleComposer composer) {
    this.deliverers = deliverers;
    this.composer = composer;
  }

  public void broadcast(CloudEvent cloudEvent) {
    var delivers = deliverers.findDelivers(cloudEvent);

    var notifications = composer.compose();

    var filteredNotifications =
        notifications.stream()
            .filter(Objects::nonNull)
            .filter(
                notification ->
                    delivers.stream()
                        .anyMatch(
                            deliverer ->
                                deliverer
                                    .getChannel()
                                    .name()
                                    .equals(notification.getChannel().name())))
            .toList();

    filteredNotifications.forEach(
        notification ->
            delivers.stream()
                .filter(
                    deliverer ->
                        deliverer.getChannel().name().equals(notification.getChannel().name()))
                .forEach(deliverer -> deliverer.deliver(notification)));
  }
}
