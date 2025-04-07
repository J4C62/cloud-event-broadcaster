package com.github.j4c62.delivery;

/**
 * Interface representing a deliverer that can deliver a cloud event.
 *
 * <p>Extends {@link Diffusible}, meaning that a deliverer is also associated with a particular
 * channel.
 */
public interface Deliverer extends Diffusible {
  /**
   * Deliver a cloud event.
   *
   * @param diffusible The event to deliver.
   */
  void deliver(Diffusible diffusible);
}
