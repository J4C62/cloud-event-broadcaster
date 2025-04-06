package dev.github.j4c62.broadcaster.core.repository;

import dev.github.j4c62.broadcaster.core.deliverer.Deliverer;
import dev.github.j4c62.broadcaster.core.dto.CloudEvent;

import java.util.List;

public interface DelivererSelector {
    List<Deliverer> findDelivers(CloudEvent cloudEvent);
}
