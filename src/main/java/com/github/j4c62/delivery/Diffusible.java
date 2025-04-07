package com.github.j4c62.delivery;

import com.github.j4c62.data.Channel;

/**
 * Interface representing an object that can be broadcasted. It includes information about the
 * channel it is associated with.
 */
public interface Diffusible {
  /**
   * Get the channel for this diffusible event.
   *
   * @return The channel associated with the event.
   */
  Channel getChannel();
}
