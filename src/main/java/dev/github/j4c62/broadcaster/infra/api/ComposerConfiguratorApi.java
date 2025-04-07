package dev.github.j4c62.broadcaster.infra.api;

import static java.util.stream.StreamSupport.stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.github.j4c62.broadcaster.core.composer.DiffusibleComposer;
import dev.github.j4c62.broadcaster.infra.delivery.dto.CloudEvent;
import io.toolisticon.fluapigen.api.*;
import io.toolisticon.fluapigen.validation.api.NotNull;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@FluentApi("ComposerConfigurator")
@NotNull
public class ComposerConfiguratorApi {

  @FluentApiBackingBean
  public interface NotificationData {
    @NotNull
    CloudEvent cloudEvent();

    @NotNull
    Map<String, Object> appConfigVariables();

    @NotNull
    Set<String> channels();

    @NotNull
    String language();
  }

  @FluentApiRoot
  @FluentApiInterface(NotificationData.class)
  public interface StepCloudEvent {
    StepAppConfigVariables forCloudEvent(
        @FluentApiBackingBeanMapping("cloudEvent") CloudEvent cloudEvent);
  }

  @FluentApiInterface(NotificationData.class)
  public interface StepAppConfigVariables {
    StepConfig usingAppConfigVariables(
        @FluentApiBackingBeanMapping("appConfigVariables") Map<String, Object> appConfigVariables);
  }

  @FluentApiInterface(NotificationData.class)
  public interface StepConfig {
    @FluentApiImplicitValue(id = "channels", value = "email")
    @FluentApiImplicitValue(id = "language", value = "en")
    StepRenderMessage withDefaultConfig();
  }

  @FluentApiInterface(NotificationData.class)
  public interface StepRenderMessage {
    @FluentApiCommand(DiffusibleComposerCommand.class)
    DiffusibleComposer composer();
  }

  @FluentApiCommand
  @UtilityClass
  public static class DiffusibleComposerCommand {

    public static DiffusibleComposer execute(NotificationData notificationData) {
      var stringObjectMap = notificationData.appConfigVariables();
      var jsonNode = new ObjectMapper().valueToTree(stringObjectMap);
      var cloudEvent = notificationData.cloudEvent();

      Set<String> channels =
          stream(
                  jsonNode
                      .path("deliverer")
                      .path(String.valueOf(cloudEvent.source()))
                      .path(cloudEvent.type())
                      .path("channels")
                      .spliterator(),
                  false)
              .map(JsonNode::asText)
              .collect(Collectors.toSet());

      notificationData.channels().addAll(channels);

      return () ->
          notificationData.channels().stream()
              .map(
                  channel ->
                      Optional.ofNullable(ChannelRegistry.get(channel))
                          .map(factory -> factory.create(jsonNode, notificationData))
                          .orElse(null))
              .toList();
    }
  }
}
