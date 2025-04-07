package dev.github.j4c62.broadcaster.infra.delivery.dto;

import static dev.github.j4c62.broadcaster.core.data.Channel.EVENT_BRIDGE;

import dev.github.j4c62.broadcaster.core.data.Channel;
import dev.github.j4c62.broadcaster.core.delivery.Diffusible;

public record EventBridge(String source, String resources, String detailType, String detail)
    implements Diffusible {
  @Override
  public Channel getChannel() {
    return EVENT_BRIDGE;
  }
}
