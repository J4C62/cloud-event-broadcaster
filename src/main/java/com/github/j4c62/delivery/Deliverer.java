package com.github.j4c62.delivery;

import com.github.j4c62.data.Channel;

/**
 * Interface representing a deliverer that can deliver a cloud event.
 *
 * <p>Extends {@link Diffusible}, meaning that a deliverer is also associated with a particular
 * channel.
 */
public interface Deliverer {
  /**
   * Deliver a cloud event.
   *
   * @param diffusible The event to deliver.
   */
  void deliver(Diffusible diffusible);

  /**
   * Get the channel for this deliverer.
   *
   * @return The channel associated with the deliverer.
   */
  Channel getChannel();
}
