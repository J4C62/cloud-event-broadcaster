package com.github.j4c62.broadcaster.spec;

import com.github.j4c62.delivery.Diffusible;
import java.util.function.Predicate;

/**
 * Interface for configuring the delivery of broadcast events. This interface allows setting a
 * filter for the {@link Diffusible} events before they are delivered.
 */
public interface BroadcasterDeliveryConfigurator {

  /**
   * Sets the filter predicate for {@link Diffusible} events.
   *
   * <p>The filter predicate will be used to decide whether or not an event should be delivered. If
   * the predicate returns {@code false}, the event will be skipped.
   *
   * @param filter a {@link Predicate} used to filter the events
   * @return the current {@link BroadcasterExecutionConfigurator} instance for further configuration
   */
  BroadcasterExecutionConfigurator filter(Predicate<Diffusible> filter);
}
