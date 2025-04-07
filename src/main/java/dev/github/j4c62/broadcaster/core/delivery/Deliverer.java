package dev.github.j4c62.broadcaster.core.delivery;

public interface Deliverer extends Diffusible {
  void deliver(Diffusible diffusible);
}
