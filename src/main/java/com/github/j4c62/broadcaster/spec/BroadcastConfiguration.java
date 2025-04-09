package com.github.j4c62.broadcaster.spec;

import com.github.j4c62.broadcaster.exception.BroadcastErrorEvent;
import java.util.function.Consumer;

/**
 * Interface combining both {@link BroadcasterDeliveryConfigurator} and {@link
 * BroadcasterExecutionConfigurator}. This interface provides methods for configuring both the
 * delivery filter and the execution logic of broadcasting.
 */
public interface BroadcastConfiguration
    extends BroadcasterDeliveryConfigurator, BroadcasterExecutionConfigurator {

  /**
   * Configures a completion handler to be triggered after the broadcasting process finishes.
   *
   * @param onComplete Runnable to handle completion.
   * @return The updated {@link BroadcasterExecutionConfigurator}.
   */
  BroadcasterExecutionConfigurator onComplete(Runnable onComplete);

  /**
   * Configures an error handler to be triggered when an exception occurs.
   *
   * @param onError Consumer to handle error events.
   * @return The updated {@link BroadcasterExecutionConfigurator}.
   */
  BroadcasterExecutionConfigurator onError(Consumer<BroadcastErrorEvent> onError);
}
