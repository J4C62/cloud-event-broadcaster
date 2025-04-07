package com.github.j4c62.broadcaster;

import com.github.j4c62.composer.DiffusibleComposer;
import com.github.j4c62.delivery.Deliverer;
import com.github.j4c62.delivery.Diffusible;
import com.github.j4c62.selector.DelivererSelector;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/** A central class responsible for broadcasting cloud events to deliverers. */
@SuppressWarnings("unused")
public class Broadcaster {
  private final DelivererSelector deliverers;
  private final DiffusibleComposer composer;
  private Predicate<Diffusible> filter = diff -> true;
  private Consumer<BroadcastEvent> onDelivery = event -> {};

  private Broadcaster(DelivererSelector deliverers, DiffusibleComposer composer) {
    this.deliverers = deliverers;
    this.composer = composer;
  }

  /**
   * Factory method to create a new {@link Broadcaster} from a {@link DelivererSelector} and a
   * {@link DiffusibleComposer}.
   *
   * @param selector The deliverer selector.
   * @param composer The diffusible composer.
   * @return A new {@link Broadcaster} instance.
   */
  public static Broadcaster from(DelivererSelector selector, DiffusibleComposer composer) {
    return new Broadcaster(selector, composer);
  }

  /**
   * Factory method to create a new {@link BroadcasterSpec} for configuring the broadcaster.
   *
   * @param selector The deliverer selector.
   * @param composer The diffusible composer.
   * @return A new {@link BroadcasterSpec} instance.
   */
  public static BroadcasterSpec spec(DelivererSelector selector, DiffusibleComposer composer) {
    return new DefaultBroadcasterSpec(selector, composer);
  }

  /**
   * Set a filter to apply to the list of notifications during broadcasting.
   *
   * @param filter The filter to apply.
   * @return The updated {@link Broadcaster}.
   */
  public Broadcaster filter(Predicate<Diffusible> filter) {
    this.filter = filter != null ? filter : this.filter;
    return this;
  }

  /**
   * Set a listener to be called each time a notification is delivered.
   *
   * @param listener The listener to execute.
   * @return The updated {@link Broadcaster}.
   */
  public Broadcaster onDelivery(Consumer<BroadcastEvent> listener) {
    this.onDelivery = listener != null ? listener : this.onDelivery;
    return this;
  }

  /**
   * Broadcast the provided cloud event to the selected deliverers.
   *
   * @param cloudEvent The cloud event to broadcast.
   */
  public void broadcast(Diffusible cloudEvent) {
    var matchingDeliverers = deliverers.findDelivers(cloudEvent);
    var deliverersByChannel =
        matchingDeliverers.stream().collect(Collectors.groupingBy(Deliverer::getChannel));

    composer.compose().stream()
        .filter(Objects::nonNull)
        .filter(filter)
        .filter(notification -> deliverersByChannel.containsKey(notification.getChannel()))
        .forEach(
            notification ->
                deliverersByChannel
                    .get(notification.getChannel())
                    .forEach(
                        deliverer -> {
                          deliverer.deliver(notification);
                          onDelivery.accept(new BroadcastEvent(deliverer, notification));
                        }));
  }
}
