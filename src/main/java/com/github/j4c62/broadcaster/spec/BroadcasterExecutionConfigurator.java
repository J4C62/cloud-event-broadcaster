package com.github.j4c62.broadcaster.spec;

import com.github.j4c62.broadcaster.BroadcastEvent;
import com.github.j4c62.broadcaster.Broadcaster;
import com.github.j4c62.delivery.Diffusible;
import com.github.j4c62.trigger.TriggerBroadcast;
import java.util.function.Consumer;

/**
 * Interface for configuring the execution of broadcasting. This interface allows setting a listener
 * for the delivery events, a trigger condition, and executing the broadcast.
 */
public interface BroadcasterExecutionConfigurator {

  /**
   * Sets the callback to be executed when a {@link BroadcastEvent} is delivered.
   *
   * <p>The {@code onDelivery} callback will be invoked with the {@link BroadcastEvent} when a
   * delivery occurs.
   *
   * @param listener a {@link Consumer} that will be called when an event is delivered
   * @return the current {@link BroadcasterExecutionConfigurator} instance for further configuration
   */
  BroadcastConfiguration onDelivery(Consumer<BroadcastEvent> listener);

  /**
   * Sets the trigger condition that determines when the broadcasting should be executed.
   *
   * <p>The trigger condition will determine whether the broadcasting should proceed or not based on
   * the state of the event.
   *
   * @param trigger a {@link TriggerBroadcast} that specifies the condition for broadcasting
   * @return the current {@link BroadcasterExecutionConfigurator} instance for further configuration
   */
  BroadcasterExecutionConfigurator when(TriggerBroadcast<Diffusible, Broadcaster> trigger);

  /**
   * Executes the broadcasting of the given {@link Diffusible} event.
   *
   * <p>This method will first evaluate the trigger condition and, if satisfied, will broadcast the
   * event. The filter and onDelivery callback are also considered during execution.
   *
   * @param diffusible the {@link Diffusible} event to be broadcasted
   */
  void execute(Diffusible diffusible);
}
