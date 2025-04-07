package dev.github.j4c62.broadcaster.infra.delivery.dto;

import static dev.github.j4c62.broadcaster.core.data.Channel.EVENT_BRIDGE;

import com.fasterxml.jackson.databind.JsonNode;
import dev.github.j4c62.broadcaster.core.data.Channel;
import dev.github.j4c62.broadcaster.core.delivery.Diffusible;
import java.net.URI;

public record CloudEvent(String id, URI source, String type, String time, JsonNode data)
    implements Diffusible {
  @Override
  public Channel getChannel() {
    return EVENT_BRIDGE;
  }
}
