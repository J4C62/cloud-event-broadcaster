package com.github.j4c62.trigger;

import com.github.j4c62.delivery.Diffusible;
import java.io.IOException;

/**
 * Interface that defines the contract for triggering broadcasts of cloud events.
 *
 * <p>The {@code trigger} method is called to trigger a broadcast for the specified {@link
 * Diffusible} cloud event.
 */
@SuppressWarnings("unused")
public interface TriggerBroadcast {
  /**
   * Trigger a broadcast for a given cloud event.
   *
   * @param cloudEvent The event to be broadcasted.
   * @throws IOException If there is an issue during the broadcasting process.
   */
  void trigger(Diffusible cloudEvent) throws IOException;
}
