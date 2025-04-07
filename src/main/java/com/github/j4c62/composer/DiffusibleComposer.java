package com.github.j4c62.composer;

import com.github.j4c62.delivery.Diffusible;
import java.util.List;

/**
 * Interface for composing a list of {@link Diffusible} objects that represent notifications to be
 * broadcasted.
 */
public interface DiffusibleComposer {
  /**
   * Compose a list of {@link Diffusible} notifications.
   *
   * @return A list of diffusible notifications.
   */
  List<Diffusible> compose();
}
