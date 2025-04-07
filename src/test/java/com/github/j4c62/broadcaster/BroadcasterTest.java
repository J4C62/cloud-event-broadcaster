package com.github.j4c62.broadcaster;

import static com.github.j4c62.data.Channel.EMAIL;
import static com.github.j4c62.data.Channel.EVENT_BRIDGE;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

import com.github.j4c62.composer.DiffusibleComposer;
import com.github.j4c62.delivery.Deliverer;
import com.github.j4c62.delivery.Diffusible;
import com.github.j4c62.selector.DelivererSelector;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BroadcasterTest {

  @Mock DelivererSelector selector;
  @Mock DiffusibleComposer composer;

  @Mock Diffusible cloudEvent;

  @Mock Diffusible emailNotification;
  @Mock Diffusible eventBridgeNotification;

  @Mock Deliverer emailDeliverer;
  @Mock Deliverer eventBridgeDeliverer;

  @Test
  void shouldDeliverOnlyMatchingNotifications() {
    when(emailDeliverer.getChannel()).thenReturn(EMAIL);
    when(emailNotification.getChannel()).thenReturn(EMAIL);
    when(selector.findDelivers(cloudEvent)).thenReturn(List.of(emailDeliverer));
    when(composer.compose()).thenReturn(List.of(emailNotification, eventBridgeNotification));

    Broadcaster broadcaster = Broadcaster.spec(selector, composer).build();

    broadcaster.broadcast(cloudEvent);

    verify(emailDeliverer).deliver(emailNotification);
    verify(eventBridgeDeliverer, never()).deliver(any());
  }

  @Test
  void shouldTriggerOnDeliveryCallback() {
    when(emailDeliverer.getChannel()).thenReturn(EMAIL);
    when(emailNotification.getChannel()).thenReturn(EMAIL);
    when(selector.findDelivers(cloudEvent)).thenReturn(List.of(emailDeliverer));
    when(composer.compose()).thenReturn(List.of(emailNotification));

    ArrayList<BroadcastEvent> deliveries = new ArrayList<>();

    Broadcaster broadcaster =
        Broadcaster.spec(selector, composer).onDelivery(deliveries::add).build();

    broadcaster.broadcast(cloudEvent);

    assertThat(deliveries).hasSize(1);
    var event = deliveries.getFirst();
    assertThat(event.deliverer()).isEqualTo(emailDeliverer);
    assertThat(event.notification()).isEqualTo(emailNotification);
  }

  @Test
  void shouldSkipWhenNoDeliverersFound() {
    when(selector.findDelivers(cloudEvent)).thenReturn(List.of());
    when(composer.compose()).thenReturn(List.of(emailNotification));

    Broadcaster broadcaster = Broadcaster.spec(selector, composer).build();
    broadcaster.broadcast(cloudEvent);

    verify(emailDeliverer, never()).deliver(any());
  }

  @Test
  void shouldApplyCustomFilter() {
    when(emailDeliverer.getChannel()).thenReturn(EMAIL);
    when(selector.findDelivers(cloudEvent)).thenReturn(List.of(emailDeliverer));
    when(composer.compose()).thenReturn(List.of(emailNotification));

    Broadcaster broadcaster = Broadcaster.spec(selector, composer).filter(diff -> false).build();

    broadcaster.broadcast(cloudEvent);

    verify(emailDeliverer, never()).deliver(any());
  }

  @Test
  void shouldSkipWhenNoMatchingDeliverersForNotifications() {
    when(eventBridgeDeliverer.getChannel()).thenReturn(EVENT_BRIDGE);
    when(selector.findDelivers(cloudEvent)).thenReturn(List.of(eventBridgeDeliverer));
    when(composer.compose()).thenReturn(List.of(emailNotification)); // EMAIL ≠ EVENT_BRIDGE

    Broadcaster broadcaster = Broadcaster.spec(selector, composer).build();
    broadcaster.broadcast(cloudEvent);

    verify(eventBridgeDeliverer, never()).deliver(any());
  }

  @Test
  void shouldDoNothingWhenComposerReturnsEmptyList() {
    when(emailDeliverer.getChannel()).thenReturn(EMAIL);
    when(selector.findDelivers(cloudEvent)).thenReturn(List.of(emailDeliverer));
    when(composer.compose()).thenReturn(List.of());

    Broadcaster broadcaster = Broadcaster.spec(selector, composer).build();
    broadcaster.broadcast(cloudEvent);

    verify(emailDeliverer, never()).deliver(any());
  }

  @Test
  void callbackShouldNotTriggerIfNoDeliveryOccurs() {
    when(selector.findDelivers(cloudEvent)).thenReturn(List.of());
    when(composer.compose()).thenReturn(List.of(emailNotification));

    List<BroadcastEvent> callbackCalls = new ArrayList<>();

    Broadcaster broadcaster =
        Broadcaster.spec(selector, composer).onDelivery(callbackCalls::add).build();

    broadcaster.broadcast(cloudEvent);

    assertThat(callbackCalls).isEmpty();
  }
}
