package com.github.j4c62.broadcaster;

import com.github.j4c62.broadcaster.exception.BroadcastErrorEvent;
import com.github.j4c62.broadcaster.spec.BroadcastConfiguration;
import com.github.j4c62.broadcaster.spec.DefaultBroadcastConfiguration;
import com.github.j4c62.composer.DiffusibleComposer;
import com.github.j4c62.data.Channel;
import com.github.j4c62.delivery.Deliverer;
import com.github.j4c62.delivery.Diffusible;
import com.github.j4c62.selector.DelivererSelector;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Broadcaster is responsible for broadcasting {@link Diffusible} notifications to matching {@link
 * Deliverer}s based on their associated {@link Channel}. It supports filtering, delivery hooks,
 * error handling, and completion callbacks.
 */
public final class Broadcaster {
  private static final Logger logger = Logger.getLogger(Broadcaster.class.getName());
  private final DelivererSelector deliverers;
  private final DiffusibleComposer composer;

  @SuppressWarnings("unused")
  private Predicate<Diffusible> filter = diff -> true;

  @SuppressWarnings("unused")
  private Consumer<BroadcastEvent> onDelivery = event -> {};

  private Consumer<BroadcastErrorEvent> onError =
      error -> logger.log(Level.SEVERE, "Error during delivery", error.error());
  private Runnable onComplete = () -> {};

  private Broadcaster(DelivererSelector deliverers, DiffusibleComposer composer) {
    this.deliverers = deliverers;
    this.composer = composer;
  }

  /**
   * Factory method to create a {@link Broadcaster} instance from the given selector and composer.
   *
   * @param selector The deliverer selector
   * @param composer The diffusible composer
   * @return A new broadcaster instance
   */
  public static Broadcaster from(DelivererSelector selector, DiffusibleComposer composer) {
    return new Broadcaster(selector, composer);
  }

  /**
   * Factory method to start building a {@link BroadcastConfiguration}.
   *
   * @param selector The deliverer selector
   * @param composer The diffusible composer
   * @return A new broadcaster specification
   */
  public static BroadcastConfiguration spec(
      DelivererSelector selector, DiffusibleComposer composer) {
    return new DefaultBroadcastConfiguration(selector, composer);
  }

  /**
   * Applies a filter to incoming {@link Diffusible} notifications.
   *
   * @param filter Predicate to determine which notifications are eligible for broadcasting
   * @return This broadcaster
   */
  public Broadcaster filter(Predicate<Diffusible> filter) {
    this.filter = Optional.ofNullable(filter).orElse(this.filter);
    return this;
  }

  /**
   * Registers a listener to be invoked upon successful delivery of a notification.
   *
   * @param listener Consumer that receives the broadcast event
   * @return This broadcaster
   */
  public Broadcaster onDelivery(Consumer<BroadcastEvent> listener) {
    this.onDelivery = Optional.ofNullable(listener).orElse(this.onDelivery);
    return this;
  }

  /**
   * Registers a listener for delivery errors.
   *
   * @param listener Consumer that receives a {@link BroadcastErrorEvent}
   * @return This broadcaster
   */
  public Broadcaster onError(Consumer<BroadcastErrorEvent> listener) {
    this.onError = Optional.ofNullable(listener).orElse(this.onError);
    return this;
  }

  /**
   * Registers a callback that will be executed after the broadcast process completes, regardless of
   * success or failure.
   *
   * @param callback Runnable to be called upon completion
   * @return This broadcaster
   */
  public Broadcaster onComplete(Runnable callback) {
    this.onComplete = Optional.ofNullable(callback).orElse(this.onComplete);
    return this;
  }

  /**
   * Broadcasts a {@link Diffusible} cloud event to all applicable {@link Deliverer}s. Applies
   * filtering, error handling, and notifies delivery and completion listeners.
   *
   * @param diffusible The cloud event to broadcast
   */
  public void broadcast(Diffusible diffusible) {
    try {
      var deliverersByChannel = groupDeliverers(diffusible);
      var events = createEvents(deliverersByChannel);
      dispatchEvents(events);
    } catch (Exception globalError) {
      onError.accept(new BroadcastErrorEvent(null, globalError));
    } finally {
      onComplete.run();
    }
  }

  private Map<Channel, List<Deliverer>> groupDeliverers(Diffusible diffusible) {
    return deliverers.findDelivers(diffusible).stream()
        .collect(Collectors.groupingBy(Deliverer::getChannel));
  }

  private Stream<BroadcastEvent> createEvents(Map<Channel, List<Deliverer>> deliverersByChannel) {
    return composer.compose().stream()
        .filter(Objects::nonNull)
        .filter(filter)
        .filter(notification -> deliverersByChannel.containsKey(notification.getChannel()))
        .flatMap(
            notification ->
                deliverersByChannel.get(notification.getChannel()).stream()
                    .map(deliverer -> new BroadcastEvent(deliverer, notification)));
  }

  private void dispatchEvents(Stream<BroadcastEvent> events) {
    events.forEach(
        event -> {
          try {
            event.deliverer().deliver(event.diffusible());
            onDelivery.accept(event);
          } catch (Exception ex) {
            onError.accept(new BroadcastErrorEvent(event, ex));
          }
        });
  }
}
