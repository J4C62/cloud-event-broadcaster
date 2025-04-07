package dev.github.j4c62.broadcaster.core.trigger;

import dev.github.j4c62.broadcaster.infra.delivery.dto.CloudEvent;
import java.io.IOException;

public interface TriggerBroadcast {
  void trigger(CloudEvent cloudEvent) throws IOException;
}
