package dev.github.j4c62.broadcaster.core.selector;

import dev.github.j4c62.broadcaster.core.delivery.Deliverer;
import dev.github.j4c62.broadcaster.infra.delivery.dto.CloudEvent;
import java.util.List;

public interface DelivererSelector {
  List<Deliverer> findDelivers(CloudEvent cloudEvent);
}
