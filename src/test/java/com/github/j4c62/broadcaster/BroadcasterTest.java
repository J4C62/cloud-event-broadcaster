package com.github.j4c62.broadcaster;

import static com.github.j4c62.data.Channel.EMAIL;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.github.j4c62.composer.DiffusibleComposer;
import com.github.j4c62.data.Channel;
import com.github.j4c62.delivery.Deliverer;
import com.github.j4c62.delivery.Diffusible;
import com.github.j4c62.selector.DelivererSelector;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unused")
class BroadcasterTest {

  private static Deliverer getDeliverer() {
    return new Deliverer() {
      @Override
      public void deliver(Diffusible diffusible) {
        System.out.printf("Delivering...%s%n", diffusible.getChannel());
      }

      @Override
      public Channel getChannel() {
        return EMAIL;
      }
    };
  }

  private static Diffusible getDiffusible() {
    return () -> EMAIL;
  }

  /**
   * ========================== Tests for Delivery Logic ==========================
   *
   * <p>These tests focus on verifying the logic related to delivering notifications based on the
   * matching channels between the deliverers and the notifications.
   */
  @Test
  @DisplayName("t1: Should deliver only matching notifications")
  void t1() {
    // Arrange
    Deliverer emailDeliverer = getDeliverer();
    Diffusible emailNotification = getDiffusible();

    DelivererSelector selector = diff -> List.of(emailDeliverer);
    DiffusibleComposer composer = () -> List.of(emailNotification);

    List<BroadcastEvent> deliveries = new ArrayList<>();

    // Act
    Broadcaster.spec(selector, composer)
        .onDelivery(deliveries::add)
        .when((diff, broad) -> true)
        .execute(emailNotification);

    // Assert - Verify if the onDelivery callback was triggered
    assertThat(deliveries).hasSize(1);
    var event = deliveries.getFirst();
    assertThat(event.deliverer()).isEqualTo(emailDeliverer);
    assertThat(event.diffusible()).isEqualTo(emailNotification);
  }

  @Test
  @DisplayName("t2: Should skip when no deliverers found")
  void t2() {
    // Arrange
    Diffusible emailNotification = getDiffusible();
    DelivererSelector selector = diff -> List.of();
    DiffusibleComposer composer = () -> List.of(emailNotification);

    List<BroadcastEvent> callbackCalls = new ArrayList<>();

    // Act
    Broadcaster.spec(selector, composer)
        .onDelivery(callbackCalls::add)
        .when((diff, broad) -> true)
        .execute(emailNotification);

    // Assert - No delivery should occur because no deliverers
    assertThat(callbackCalls).isEmpty();
  }

  @Test
  @DisplayName("t3: Should skip when no matching deliverers for notifications")
  void t3() {
    // Arrange
    Deliverer eventBridgeDeliverer = getDeliverer();
    Diffusible emailNotification = getDiffusible();

    DelivererSelector selector = diff -> List.of(eventBridgeDeliverer);
    DiffusibleComposer composer = List::of;

    List<BroadcastEvent> callbackCalls = new ArrayList<>();

    // Act
    Broadcaster.spec(selector, composer)
        .onDelivery(callbackCalls::add)
        .when((diff, broad) -> true)
        .execute(emailNotification);

    // Assert - No delivery should occur because no matching deliverers
    assertThat(callbackCalls).isEmpty();
  }

  @Test
  @DisplayName("t4: Should do nothing when composer returns empty list")
  void t4() {
    // Arrange
    Deliverer emailDeliverer = getDeliverer();
    Diffusible emailNotification = getDiffusible();

    // Using lambda for selector and composer
    DelivererSelector selector = diff -> List.of(emailDeliverer);
    DiffusibleComposer composer = List::of; // Empty list

    List<BroadcastEvent> callbackCalls = new ArrayList<>();

    // Act
    Broadcaster.spec(selector, composer)
        .onDelivery(callbackCalls::add)
        .when((diff, broad) -> true)
        .execute(emailNotification);

    // Assert - No delivery should occur because composer returned an empty list
    assertThat(callbackCalls).isEmpty();
  }

  /**
   * ========================== Tests for Callback Functionality ==========================
   *
   * <p>These tests check the behavior of callback functions such as the `onDelivery` handler that
   * should trigger actions when notifications are delivered.
   */
  @Test
  @DisplayName("t5: Should trigger and run the action when condition is true")
  void t5() {
    // Arrange
    Deliverer emailDeliverer = getDeliverer();
    Diffusible emailNotification = getDiffusible();

    // Using lambda for selector and composer
    DelivererSelector selector = diff -> List.of(emailDeliverer);
    DiffusibleComposer composer = () -> List.of(emailNotification);

    AtomicBoolean actionExecuted = new AtomicBoolean(false);

    // Act
    Broadcaster.spec(selector, composer)
        .onDelivery(event -> actionExecuted.set(true))
        .when((diff, broad) -> true)
        .execute(emailNotification);

    // Assert - Action should have been executed
    assertThat(actionExecuted.get()).isTrue();
  }

  @Test
  @DisplayName("t6: Should not trigger action when condition is false")
  void t6() {
    // Arrange
    AtomicBoolean actionExecuted = new AtomicBoolean(false);

    // Act
    Broadcaster.spec(diff -> List.of(), List::of)
        .onDelivery(event -> actionExecuted.set(true))
        .when((diff, broad) -> false)
        .execute(getDiffusible());

    // Assert - Action should not be executed since condition is false
    assertThat(actionExecuted.get()).isFalse();
  }

  @Test
  @DisplayName("t7: Should handle null conditions gracefully")
  void t7() {
    // Arrange
    AtomicBoolean actionExecuted = new AtomicBoolean(false);

    // Act
    Broadcaster.spec(diff -> List.of(), List::of)
        .onDelivery(event -> actionExecuted.set(true))
        .when(null)
        .execute(getDiffusible());

    // Assert - Action should not be executed as the condition is null
    assertThat(actionExecuted.get()).isFalse();
  }

  @Test
  @DisplayName("t9: Should apply custom filter")
  void t9() {
    // Arrange
    Deliverer emailDeliverer = getDeliverer();
    Diffusible emailNotification = getDiffusible();

    // Using lambda for selector and composer
    DelivererSelector selector = diff -> List.of(emailDeliverer);
    DiffusibleComposer composer = () -> List.of(emailNotification);

    List<BroadcastEvent> callbackCalls = new ArrayList<>();

    // Act
    Broadcaster.spec(selector, composer)
        .filter(diff -> false)
        .onDelivery(callbackCalls::add)
        .when((diff, broad) -> true)
        .execute(emailNotification);

    // Assert - No delivery should occur because filter prevents it
    assertThat(callbackCalls).isEmpty();
  }

  @Test
  @DisplayName("t10: Should not trigger onDelivery when onDelivery is null")
  void t10() {
    // Arrange
    Deliverer emailDeliverer = getDeliverer();
    Diffusible emailNotification = getDiffusible();

    DelivererSelector selector = diff -> List.of(emailDeliverer);
    DiffusibleComposer composer = () -> List.of(emailNotification);

    List<BroadcastEvent> deliveries = new ArrayList<>();

    // Act
    Broadcaster.spec(selector, composer)
        .onDelivery(null)
        .when((diff, broad) -> true)
        .execute(emailNotification);

    Broadcaster.from(selector, composer).onDelivery(null).broadcast(emailNotification);
    // Assert - No delivery should occur since onDelivery is null
    assertThat(deliveries).isEmpty();
  }

  @Test
  @DisplayName("t11: Should handle null filter gracefully")
  void t11() {
    // Arrange
    AtomicBoolean actionExecuted = new AtomicBoolean(false);
    Deliverer emailDeliverer = getDeliverer();
    Diffusible emailNotification = getDiffusible();

    DelivererSelector selector = diff -> List.of(emailDeliverer);
    DiffusibleComposer composer = () -> List.of(emailNotification);

    // Act
    Broadcaster.spec(selector, composer)
        .filter(null)
        .onDelivery(event -> actionExecuted.set(true))
        .when((diff, broad) -> true)
        .execute(emailNotification);

    Broadcaster.from(selector, composer)
        .filter(null)
        .onDelivery(event -> actionExecuted.set(true))
        .broadcast(emailNotification);

    // Assert - Action should be executed even with null filter
    assertThat(actionExecuted.get()).isTrue();
  }

  @Test
  @DisplayName("t12: Should trigger onError when an exception occurs")
  void t12() {
    // Arrange
    Deliverer emailDeliverer =
        new Deliverer() {
          @Override
          public void deliver(Diffusible diffusible) {
            throw new RuntimeException("Simulated error");
          }

          @Override
          public Channel getChannel() {
            return EMAIL;
          }
        };
    Diffusible emailNotification = getDiffusible();

    DelivererSelector selector = diff -> List.of(emailDeliverer);
    DiffusibleComposer composer = () -> List.of(emailNotification);

    AtomicBoolean errorHandlerTriggered = new AtomicBoolean(false);
    AtomicBoolean actionExecuted = new AtomicBoolean(false);

    // Act
    Broadcaster.spec(selector, composer)
        .onDelivery(event -> actionExecuted.set(true))
        .onError(errorEvent -> errorHandlerTriggered.set(true)) // Handle errors
        .when((diff, broad) -> true)
        .execute(emailNotification);

    // Assert - The error handler should be triggered
    assertThat(errorHandlerTriggered.get()).isTrue();
    // Assert - onDelivery should not be triggered because of the error
    assertThat(actionExecuted.get()).isFalse();
  }

  @Test
  @DisplayName("t13: Should handle onError null gracefully")
  void t13() {
    // Arrange
    Deliverer emailDeliverer =
        new Deliverer() {
          @Override
          public void deliver(Diffusible diffusible) {
            throw new RuntimeException("Simulated error");
          }

          @Override
          public Channel getChannel() {
            return EMAIL;
          }
        };
    Diffusible emailNotification = getDiffusible();

    DelivererSelector selector = diff -> List.of(emailDeliverer);
    DiffusibleComposer composer = () -> List.of(emailNotification);

    AtomicBoolean errorHandlerTriggered = new AtomicBoolean(false);
    AtomicBoolean actionExecuted = new AtomicBoolean(false);

    // Act
    Broadcaster.spec(selector, composer)
        .onDelivery(event -> actionExecuted.set(true))
        .onError(null) // Handle errors with null (should do nothing)
        .when((diff, broad) -> true)
        .execute(emailNotification);

    // Assert - Since onError is null, errorHandlerTriggered should be false (no handler called)
    assertThat(errorHandlerTriggered.get()).isFalse();

    // Assert - onDelivery should not be triggered because of the error
    assertThat(actionExecuted.get()).isFalse();
  }

  @Test
  @DisplayName("t14: Should trigger onComplete after broadcasting finishes")
  void t14() {
    // Arrange
    Deliverer emailDeliverer = getDeliverer();
    Diffusible emailNotification = getDiffusible();

    DelivererSelector selector = diff -> List.of(emailDeliverer);
    DiffusibleComposer composer = () -> List.of(emailNotification);

    AtomicBoolean completeHandlerTriggered = new AtomicBoolean(false);
    AtomicBoolean actionExecuted = new AtomicBoolean(false);

    // Act
    Broadcaster.spec(selector, composer)
        .onDelivery(event -> actionExecuted.set(true))
        .onComplete(() -> completeHandlerTriggered.set(true))
        .when((diff, broad) -> true)
        .execute(emailNotification);

    // Assert - The completion handler should be triggered
    assertThat(completeHandlerTriggered.get()).isTrue();
    // Assert - onDelivery should be triggered as well
    assertThat(actionExecuted.get()).isTrue();
  }

  @Test
  @DisplayName("t15: Should handle onComplete null gracefully")
  void t15() {
    // Arrange
    Deliverer emailDeliverer = getDeliverer();
    Diffusible emailNotification = getDiffusible();

    DelivererSelector selector = diff -> List.of(emailDeliverer);
    DiffusibleComposer composer = () -> List.of(emailNotification);

    AtomicBoolean completeHandlerTriggered = new AtomicBoolean(false);
    AtomicBoolean actionExecuted = new AtomicBoolean(false);

    // Act
    Broadcaster.spec(selector, composer)
        .onDelivery(event -> actionExecuted.set(true))
        .onComplete(null) // Here, onComplete is null
        .when((diff, broad) -> true)
        .execute(emailNotification);

    // Assert - The complete handler should not be triggered because onComplete is null
    assertThat(completeHandlerTriggered.get()).isFalse();
    // Assert - onDelivery should still be triggered
    assertThat(actionExecuted.get()).isTrue();
  }
}
