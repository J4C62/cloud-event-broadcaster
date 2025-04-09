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
   * Executes one of the given actions depending on whether the predicate evaluates to true or
   * false.
   *
   * @param t The first input argument.
   * @param u The second input argument.
   * @param ifTrue Action to run if the predicate is true.
   * @param ifFalse Action to run if the predicate is false.
   */
  default void ifRunOrElse(T t, U u, Runnable ifTrue, Runnable ifFalse) {
    if (test(t, u)) {
      ifTrue.run();
    } else {
      ifFalse.run();
    }
  }
}
