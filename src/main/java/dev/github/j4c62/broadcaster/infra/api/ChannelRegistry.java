package dev.github.j4c62.broadcaster.infra.api;

import dev.github.j4c62.broadcaster.infra.module.DiffusibleModule;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ChannelRegistry {
  private static final Map<String, DiffusibleModule.DiffusibleFactory> registry = new HashMap<>();

  public static boolean isRegistered(Set<String> channels) {
    return registry.keySet().containsAll(channels);
  }

  public static void register(String channels, DiffusibleModule.DiffusibleFactory handler) {
    registry.put(channels, handler);
  }

  public static DiffusibleModule.DiffusibleFactory get(String channels) {
    return registry.getOrDefault(channels, null);
  }
}
