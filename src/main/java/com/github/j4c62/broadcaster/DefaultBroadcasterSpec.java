package com.github.j4c62.broadcaster;

import com.github.j4c62.composer.DiffusibleComposer;
import com.github.j4c62.delivery.Diffusible;
import com.github.j4c62.selector.DelivererSelector;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/** Default implementation of the {@link BroadcasterSpec} that provides the actual configuration. */
public class DefaultBroadcasterSpec implements BroadcasterSpec {
  private final DelivererSelector selector;
  private final DiffusibleComposer composer;
  private Predicate<Diffusible> filter = diff -> true;
  private Consumer<BroadcastEvent> onDelivery = event -> {};

  /**
   * Constructor for creating a {@link DefaultBroadcasterSpec} with a specific {@link
   * DelivererSelector} and {@link DiffusibleComposer}.
   *
   * @param selector The deliverer selector to use.
   * @param composer The composer to use.
   */
  public DefaultBroadcasterSpec(DelivererSelector selector, DiffusibleComposer composer) {
    this.selector = Objects.requireNonNull(selector);
    this.composer = Objects.requireNonNull(composer);
  }

  @Override
  public BroadcasterSpec filter(Predicate<Diffusible> filter) {
    this.filter = filter != null ? filter : this.filter;
    return this;
  }

  @Override
  public BroadcasterSpec onDelivery(Consumer<BroadcastEvent> listener) {
    this.onDelivery = listener != null ? listener : this.onDelivery;
    return this;
  }

  @Override
  public Broadcaster build() {
    return Broadcaster.from(selector, composer).filter(filter).onDelivery(onDelivery);
  }
}
