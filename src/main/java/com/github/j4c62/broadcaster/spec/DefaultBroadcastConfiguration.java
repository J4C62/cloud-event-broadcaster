package com.github.j4c62.broadcaster.spec;

import com.github.j4c62.broadcaster.BroadcastEvent;
import com.github.j4c62.broadcaster.Broadcaster;
import com.github.j4c62.broadcaster.exception.BroadcastErrorEvent;
import com.github.j4c62.composer.DiffusibleComposer;
import com.github.j4c62.delivery.Diffusible;
import com.github.j4c62.selector.DelivererSelector;
import com.github.j4c62.trigger.TriggerBroadcast;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DefaultBroadcastConfiguration implements BroadcastConfiguration {

  private static final Logger logger = Logger.getLogger(DefaultBroadcastConfiguration.class.getName());
  private final DelivererSelector selector;
  private final DiffusibleComposer composer;

  @SuppressWarnings("unused")
  private Predicate<Diffusible> filter = diff -> true;

  @SuppressWarnings("unused")
  private Consumer<BroadcastEvent> onDelivery = event -> {};

  @SuppressWarnings("unused")
  private Consumer<BroadcastErrorEvent> onError = error -> {};

  private Runnable onComplete = () -> {};
  private TriggerBroadcast<Diffusible, Broadcaster> trigger;

  public DefaultBroadcastConfiguration(DelivererSelector selector, DiffusibleComposer composer) {
    this.selector = Objects.requireNonNull(selector);
    this.composer = Objects.requireNonNull(composer);
  }

  @Override
  public BroadcasterExecutionConfigurator filter(Predicate<Diffusible> filter) {
    this.filter = Optional.ofNullable(filter).orElse(this.filter);
    return this;
  }

  @Override
  public BroadcastConfiguration onDelivery(Consumer<BroadcastEvent> listener) {
    this.onDelivery = Optional.ofNullable(listener).orElse(this.onDelivery);
    return this;
  }

  @Override
  public BroadcasterExecutionConfigurator when(TriggerBroadcast<Diffusible, Broadcaster> trigger) {
    this.trigger = trigger;
    return this;
  }

  /**
   * Configures an error handler to be triggered when an exception occurs.
   *
   * @param onError Consumer to handle error events.
   * @return The updated {@link BroadcasterExecutionConfigurator}.
   */
  @Override
  public BroadcasterExecutionConfigurator onError(Consumer<BroadcastErrorEvent> onError) {
    this.onError = Optional.ofNullable(onError).orElse(this.onError);
    return this;
  }

  /**
   * Configures a completion handler to be triggered after the broadcasting process finishes.
   *
   * @param onComplete Runnable to handle completion.
   * @return The updated {@link BroadcasterExecutionConfigurator}.
   */
  @Override
  public BroadcasterExecutionConfigurator onComplete(Runnable onComplete) {
    this.onComplete = Optional.ofNullable(onComplete).orElse(this.onComplete);
    return this;
  }

  @Override
  public void execute(Diffusible cloudEvent) {
    Broadcaster broadcaster =
        Broadcaster.from(selector, composer)
            .filter(filter)
            .onDelivery(onDelivery)
            .onError(onError)
            .onComplete(onComplete);

    TriggerBroadcast<Diffusible, Broadcaster> effectiveTrigger =
        Optional.ofNullable(trigger).orElse((diff, bcast) -> false);

    effectiveTrigger.ifRunOrElse(
        cloudEvent,
        broadcaster,
        () -> broadcaster.broadcast(cloudEvent),
        () -> logger.log(Level.WARNING, "Broadcast disabled"));
  }
}
