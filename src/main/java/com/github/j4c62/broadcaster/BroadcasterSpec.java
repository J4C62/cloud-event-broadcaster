package com.github.j4c62.broadcaster;

import com.github.j4c62.delivery.Diffusible;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A fluent specification builder for creating a {@link Broadcaster} with custom filters and actions
 * to execute on each delivery.
 */
public interface BroadcasterSpec {
  /**
   * Add a filter to selectively include or exclude notifications during broadcasting.
   *
   * @param filter The filter to apply.
   * @return The updated {@link BroadcasterSpec}.
   */
  BroadcasterSpec filter(Predicate<Diffusible> filter);

  /**
   * Set a listener that will be called every time a notification is delivered.
   *
   * @param listener The listener to execute on each delivery.
   * @return The updated {@link BroadcasterSpec}.
   */
  BroadcasterSpec onDelivery(Consumer<BroadcastEvent> listener);

  /**
   * Build and return the configured {@link Broadcaster}.
   *
   * @return The created {@link Broadcaster}.
   */
  Broadcaster build();
}
