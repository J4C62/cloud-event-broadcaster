package com.github.j4c62.trigger;

import java.util.function.BiPredicate;

/**
 * Interface that defines the contract for triggering broadcasts of cloud events.
 *
 * <p>This interface extends {@link BiPredicate} and provides a method for conditionally triggering
 * the broadcasting of a cloud event. The {@code runIf} method is used to evaluate the predicate and
 * execute the given action if the condition is met.
 *
 * @param <T> the type of the first argument to the predicate (e.g., the event type)
 * @param <U> the type of the second argument to the predicate (e.g., the broadcaster or context)
 */
@SuppressWarnings("unused")
public interface TriggerBroadcast<T, U> extends BiPredicate<T, U> {

  /**
   * Evaluates the predicate using the provided arguments and executes the given action if the
   * predicate returns {@code true}.
   *
   * <p>This method provides a convenient way to run the action only when the trigger condition is
   * satisfied. The trigger condition is defined by the {@link #test(Object, Object)} method
   * inherited from {@link BiPredicate}.
   *
   * @param t the first argument to the predicate (e.g., the event)
   * @param u the second argument to the predicate (e.g., the broadcaster or context)
   * @param action the action to be executed if the condition is {@code true}
   */
  default void runIf(T t, U u, Runnable action) {
    if (test(t, u)) {
      action.run();
    }
  }
}
