package dev.github.j4c62.broadcaster.infra.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import dev.github.j4c62.broadcaster.core.deliverer.EmailDeliverer;
import dev.github.j4c62.broadcaster.core.dto.CloudEvent;
import dev.github.j4c62.broadcaster.core.dto.Email;
import dev.github.j4c62.broadcaster.core.dto.EventBridge;
import dev.github.j4c62.broadcaster.core.repository.DelivererSelector;
import dev.github.j4c62.broadcaster.core.service.Broadcaster;
import dev.github.j4c62.broadcaster.infra.module.ChannelModule;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static dev.github.j4c62.broadcaster.infra.api.ChannelRegistry.isRegistered;
import static dev.github.j4c62.broadcaster.infra.api.ChannelRegistry.register;
import static dev.github.j4c62.broadcaster.infra.api.StarterApiNotification.forCloudEvent;


class NotificationFluentApiTest {

    @Inject
    @Named("eventBridge")
    public ChannelModule.ChannelHandler eventBridge;

    @Inject
    @Named("email")
    public ChannelModule.ChannelHandler email;


    @BeforeEach
    void setUp() {
        Injector injector = Guice.createInjector(new ChannelModule());
        injector.injectMembers(this);
    }


    @Test
    void name() {
        Map<String, Object> appConfigVariables = Map.of(
                "publishers", Map.of(
                        "example.com",
                        Map.of("example.success",
                                Map.of("channels",
                                        new String[]{"email", "event_bridge"}))
                ),
                "config", Map.of(
                        "example.com", Map.of(
                                "example.success", Map.of(
                                        "email", Map.of(
                                                "from", "example@example",
                                                "target", "target@target",
                                                "subject", "Example"
                                        ),
                                        "event_bridge", Map.of(
                                                "source", "aws.events",
                                                "detail_type", "ExampleEvent",
                                                "time", "2021-06-01T00:00:00Z",
                                                "resources", List.of("arn:aws:events:region:account-id:rule/rule-name"),
                                                "detail", Map.of(
                                                        "example", "value"
                                                )
                                        )
                                )
                        )
                ),
                "templates",
                Map.of("example.com",
                        Map.of("example.success",
                                Map.of("email",
                                        Map.of("en",
                                                Map.of(
                                                        "content", "<!DOCTYPE html>\n<html xmlns:th=\"http://www.thymeleaf.org\" lang=\"es\">\n<head>\n    <title>Notificación de Transacción</title>\n</head>\n<body>\n<h1>Notificación de Transacción</h1>\n<p>Hola <span th:text=\"${userName}\">Usuario</span>,</p>\n<p>Tu reciente transacción de <span th:text=\"${transactionAmount}\">Cantidad</span> ha sido procesada con éxito.</p>\n<p>¡Gracias por usar nuestro servicio!</p>\n</body>\n</html>\n",
                                                        "engine", "Thymeleaf"
                                                ))))));


        var variables = new HashMap<String, Object>();
        variables.put("userName", "Juan");
        variables.put("transactionAmount", 250.75);

        var mapper = new ObjectMapper();


        var cloudEvent = new CloudEvent(
                "12345",
                URI.create("example.com"),
                "example.success",
                "",
                mapper.valueToTree(variables)

        );

        if (!isRegistered(Set.of("email", "event_bridge"))) {
            register("event_bridge", eventBridge);
            register("email", email);

        }


        var composer = forCloudEvent(cloudEvent)
                .usingAppConfigVariables(appConfigVariables)
                .withDefaultConfig()
                .composer();


        var notifications = composer.compose();

        DelivererSelector delivererSelector = (_) -> List.of(new EmailDeliverer());

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