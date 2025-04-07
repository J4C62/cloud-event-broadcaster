package dev.github.j4c62.broadcaster.infra.module;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.*;
import dev.github.j4c62.broadcaster.core.delivery.Diffusible;
import dev.github.j4c62.broadcaster.infra.api.ComposerConfiguratorApi;
import dev.github.j4c62.broadcaster.infra.delivery.dto.Email;
import dev.github.j4c62.broadcaster.infra.delivery.dto.EventBridge;
import freemarker.template.TemplateException;
import jakarta.inject.Named;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import lombok.Setter;

@Setter
public class DiffusibleModule extends AbstractModule {
  @Inject
  @Named("TemplateEngines")
  private Map<String, TemplateModule.NotificationTemplateEngine> templateEngines;

  public DiffusibleModule() {
    Injector injector = Guice.createInjector(new TemplateModule());
    injector.injectMembers(this);
  }

  @Provides
  @Named("eventBridge")
  @SuppressWarnings("unused")
  public DiffusibleFactory eventBridge() {
    return (root, data) -> {
      var config =
          root.path("config")
              .path(String.valueOf(data.cloudEvent().source()))
              .path(data.cloudEvent().type())
              .path("event_bridge");

      return new EventBridge(
          config.path("source").asText(),
          config.path("resources").toString(),
          config.path("detail_type").asText(),
          config.path("detail").toString());
    };
  }

  @Provides
  @Named("email")
  @SuppressWarnings("unused")
  public DiffusibleFactory email() {
    return (root, data) -> {
      var cloudEvent = data.cloudEvent();
      var config =
          root.path("config")
              .path(String.valueOf(cloudEvent.source()))
              .path(cloudEvent.type())
              .path("email");

      var template =
          root.path("templates")
              .path(String.valueOf(cloudEvent.source()))
              .path(cloudEvent.type())
              .path("email")
              .path(data.language())
              .path("content")
              .asText();

      var engine =
          root.path("templates")
              .path(String.valueOf(cloudEvent.source()))
              .path(cloudEvent.type())
              .path("email")
              .path(data.language())
              .path("engine")
              .asText();

      String body;
      try {
        body =
            templateEngines
                .get(engine)
                .process(template, cloudEvent.data(), Locale.of(data.language()));
      } catch (IOException | TemplateException e) {
        throw new RuntimeException(e);
      }

      return new Email(
          config.path("from").asText(),
          config.path("target").asText(),
          config.path("subject").asText(),
          body);
    };
  }

  public interface DiffusibleFactory {
    Diffusible create(JsonNode root, ComposerConfiguratorApi.NotificationData data);
  }
}
