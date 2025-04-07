package dev.github.j4c62.broadcaster.core.composer;

import dev.github.j4c62.broadcaster.core.delivery.Diffusible;
import java.util.List;

public interface DiffusibleComposer {
  List<Diffusible> compose();
}
