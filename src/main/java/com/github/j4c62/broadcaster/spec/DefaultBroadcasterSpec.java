package com.github.j4c62.broadcaster.spec;

import com.github.j4c62.broadcaster.BroadcastEvent;
import com.github.j4c62.broadcaster.Broadcaster;
import com.github.j4c62.composer.DiffusibleComposer;
import com.github.j4c62.delivery.Diffusible;
import com.github.j4c62.selector.DelivererSelector;
import com.github.j4c62.trigger.TriggerBroadcast;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class DefaultBroadcasterSpec implements BroadcasterSpec {

  private final DelivererSelector selector;
  private final DiffusibleComposer composer;
  private Predicate<Diffusible> filter = diff -> true;
  private Consumer<BroadcastEvent> onDelivery = event -> {};
  private TriggerBroadcast<Diffusible, Broadcaster> trigger;

  public DefaultBroadcasterSpec(DelivererSelector selector, DiffusibleComposer composer) {
    this.selector = Objects.requireNonNull(selector);
    this.composer = Objects.requireNonNull(composer);
  }

  @Override
  public BroadcasterExecutionConfigurator filter(Predicate<Diffusible> filter) {
    this.filter = filter != null ? filter : this.filter;
    return this;
  }

  @Override
  public BroadcasterExecutionConfigurator onDelivery(Consumer<BroadcastEvent> listener) {
    this.onDelivery = listener != null ? listener : this.onDelivery;
    return this;
  }

  @Override
  public BroadcasterExecutionConfigurator when(TriggerBroadcast<Diffusible, Broadcaster> trigger) {
    this.trigger = trigger;
    return this;
  }

  @Override
  public void execute(Diffusible cloudEvent) {
    Broadcaster broadcaster =
        Broadcaster.from(selector, composer).filter(filter).onDelivery(onDelivery);

    TriggerBroadcast<Diffusible, Broadcaster> effectiveTrigger =
        Optional.ofNullable(trigger).orElse((diff, bcast) -> false);

    effectiveTrigger.runIf(cloudEvent, broadcaster, () -> broadcaster.broadcast(cloudEvent));
  }
}
