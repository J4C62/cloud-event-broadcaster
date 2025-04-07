package dev.github.j4c62.broadcaster.infra.delivery.deliverer;

import static dev.github.j4c62.broadcaster.core.data.Channel.EMAIL;

import dev.github.j4c62.broadcaster.core.data.Channel;
import dev.github.j4c62.broadcaster.core.delivery.Deliverer;
import dev.github.j4c62.broadcaster.core.delivery.Diffusible;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailDeliverer implements Deliverer {

  @Override
  public void deliver(Diffusible diffusible) {
    log.info("Delivering...{}", diffusible);
  }

  @Override
  public Channel getChannel() {
    return EMAIL;
  }
}
