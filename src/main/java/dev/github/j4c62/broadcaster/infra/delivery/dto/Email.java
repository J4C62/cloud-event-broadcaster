package dev.github.j4c62.broadcaster.infra.delivery.dto;

import static dev.github.j4c62.broadcaster.core.data.Channel.EMAIL;

import dev.github.j4c62.broadcaster.core.data.Channel;
import dev.github.j4c62.broadcaster.core.delivery.Diffusible;

public record Email(String from, String to, String subject, String body) implements Diffusible {
  @Override
  public Channel getChannel() {
    return EMAIL;
  }
}
