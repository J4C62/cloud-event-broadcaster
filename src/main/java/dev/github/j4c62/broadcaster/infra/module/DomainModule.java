package dev.github.j4c62.broadcaster.infra.module;

import static dev.github.j4c62.broadcaster.infra.api.ChannelRegistry.isRegistered;
import static dev.github.j4c62.broadcaster.infra.api.ChannelRegistry.register;
import static dev.github.j4c62.broadcaster.infra.api.ComposerConfigurator.forCloudEvent;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provides;
import dev.github.j4c62.broadcaster.core.broadcaster.Broadcaster;
import dev.github.j4c62.broadcaster.core.selector.DelivererSelector;
import dev.github.j4c62.broadcaster.core.trigger.TriggerBroadcast;
import dev.github.j4c62.broadcaster.infra.adapter.JsonConfig;
import dev.github.j4c62.broadcaster.infra.delivery.deliverer.EmailDeliverer;
import jakarta.inject.Named;
import java.util.List;
import java.util.Set;

public class DomainModule extends AbstractModule {

  @Inject
  @Named("eventBridge")
  public DiffusibleModule.DiffusibleFactory eventBridge;

  @Inject
  @Named("email")
  public DiffusibleModule.DiffusibleFactory email;

  public DomainModule() {
    Injector injector = Guice.createInjector(new DiffusibleModule());
    injector.injectMembers(this);
  }

  @Provides
  @Named("Trigger")
  @SuppressWarnings("unused")
  public TriggerBroadcast provideEventBridgeHandler() {
    return cloudEvent -> {
      DelivererSelector delivererSelector = v -> List.of(new EmailDeliverer());
      if (!isRegistered(Set.of("email", "event_bridge"))) {
        register("event_bridge", eventBridge);
        register("email", email);
      }
      final var composer =
          forCloudEvent(cloudEvent)
              .usingAppConfigVariables(new JsonConfig().getVariables())
              .withDefaultConfig()
              .composer();

      new Broadcaster(delivererSelector, composer).broadcast(cloudEvent);
    };
  }
}
