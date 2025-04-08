package com.github.j4c62.selector;

import com.github.j4c62.delivery.Deliverer;
import com.github.j4c62.delivery.Diffusible;
import java.util.List;

/** Interface to select a list of deliverers based on the given cloud event. */
public interface DelivererSelector {
  /**
   * Find the deliverers for the given cloud event.
   *
   * @param diffusible The cloud event to select deliverers for.
   * @return A list of {@link Deliverer}s that should deliver the event.
   */
  List<Deliverer> findDelivers(Diffusible diffusible);
}
