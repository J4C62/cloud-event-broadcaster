package dev.github.j4c62.broadcaster.infra.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.toolisticon.fluapigen.api.*;
import io.toolisticon.fluapigen.validation.api.NotNull;
import dev.github.j4c62.broadcaster.core.composer.NotificationComposer;
import dev.github.j4c62.broadcaster.core.dto.CloudEvent;
import dev.github.j4c62.broadcaster.core.dto.Notification;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.StreamSupport.stream;

@FluentApi("StarterApiNotification")
@NotNull
public class NotificationFluentApi {


    @FluentApiBackingBean
    public interface NotificationData extends Notification {
        @NotNull
        CloudEvent cloudEvent();

        @NotNull
        Map<String, Object> appConfigVariables();

        @NotNull
        Set<String> channelsPath();

        @NotNull
        String languagePath();

    }

    @FluentApiRoot
    @FluentApiInterface(NotificationData.class)
    public interface StepCloudEvent {
        StepAppConfigVariables forCloudEvent(
                @FluentApiBackingBeanMapping("cloudEvent") CloudEvent cloudEvent
        );
    }

    @FluentApiInterface(NotificationData.class)
    public interface StepAppConfigVariables {
        StepConfig usingAppConfigVariables(
                @FluentApiBackingBeanMapping("appConfigVariables") Map<String, Object> appConfigVariables
        );
    }

    @FluentApiInterface(NotificationData.class)
    public interface StepConfig {
        @FluentApiImplicitValue(id = "channelsPath", value = "email")
        @FluentApiImplicitValue(id = "languagePath", value = "en")
        StepRenderMessage withDefaultConfig();
    }


    @FluentApiInterface(NotificationData.class)
    public interface StepRenderMessage {
        @FluentApiCommand(RenderMessage.class)
        NotificationComposer composer();
    }


    @FluentApiCommand
    public static class RenderMessage {

        public static NotificationComposer execute(NotificationData notificationData) {
            var stringObjectMap = notificationData.appConfigVariables();
            var jsonNode = new ObjectMapper().valueToTree(stringObjectMap);
            var cloudEvent = notificationData.cloudEvent();

            Set<String> channels = stream(
                    jsonNode.path("publishers")
                            .path(String.valueOf(cloudEvent.source()))
                            .path(cloudEvent.type())
                            .path("channels")
                            .spliterator(),
                    false)
                    .map(JsonNode::asText)
                    .collect(Collectors.toSet());

            notificationData.channelsPath().addAll(channels);

            return () -> notificationData.channelsPath()
                    .stream()
                    .map(channel -> ChannelRegistry.get(channel).handle(jsonNode, notificationData))
                    .toList();

        }


    }


}
