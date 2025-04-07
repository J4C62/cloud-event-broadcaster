package dev.github.j4c62.broadcaster.infra.api;

import static dev.github.j4c62.broadcaster.infra.api.ChannelRegistry.isRegistered;
import static dev.github.j4c62.broadcaster.infra.api.ChannelRegistry.register;
import static dev.github.j4c62.broadcaster.infra.api.ComposerConfigurator.forCloudEvent;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import dev.github.j4c62.broadcaster.core.broadcaster.Broadcaster;
import dev.github.j4c62.broadcaster.core.selector.DelivererSelector;
import dev.github.j4c62.broadcaster.infra.adapter.JsonConfig;
import dev.github.j4c62.broadcaster.infra.delivery.deliverer.EmailDeliverer;
import dev.github.j4c62.broadcaster.infra.delivery.dto.CloudEvent;
import dev.github.j4c62.broadcaster.infra.delivery.dto.Email;
import dev.github.j4c62.broadcaster.infra.delivery.dto.EventBridge;
import dev.github.j4c62.broadcaster.infra.module.DiffusibleModule;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DiffusibleFluentApiTest {

  @Inject
  @Named("eventBridge")
  public DiffusibleModule.DiffusibleFactory eventBridge;

  @Inject
  @Named("email")
  public DiffusibleModule.DiffusibleFactory email;

  @BeforeEach
  void setUp() {
    Injector injector = Guice.createInjector(new DiffusibleModule());
    injector.injectMembers(this);
  }

  @Test
  void name() throws IOException {
    var mapper = new ObjectMapper();
    var appConfigVariables = new JsonConfig().getVariables();

    var variables = new HashMap<String, Object>();
    variables.put("userName", "Juan");
    variables.put("transactionAmount", 250.75);

    var cloudEvent =
        new CloudEvent(
            "12345",
            URI.create("example.com"),
            "example.success",
            "",
            mapper.valueToTree(variables));

    if (!isRegistered(Set.of("email", "event_bridge"))) {
      register("event_bridge", eventBridge);
      register("email", email);
    }

    var composer =
        forCloudEvent(cloudEvent)
            .usingAppConfigVariables(appConfigVariables)
            .withDefaultConfig()
            .composer();

    var notifications = composer.compose();

    DelivererSelector delivererSelector = v -> List.of(new EmailDeliverer());

    var broadcaster = new Broadcaster(delivererSelector, composer);

    broadcaster.broadcast(cloudEvent);

    assertThat((EventBridge) notifications.getFirst())
        .as("Check the EventBridgeNotification")
        .extracting(EventBridge::source, EventBridge::detailType)
        .containsExactly("aws.events", "ExampleEvent");

    assertThat((Email) notifications.getLast())
        .as("Check the EmailNotification")
        .extracting(Email::subject, Email::from, Email::to)
        .containsExactly("Example", "example@example", "target@target");

    assertThat(((Email) notifications.getLast()).body())
        .as("Check body")
        .contains("Juan")
        .contains("250.75");
  }
}
