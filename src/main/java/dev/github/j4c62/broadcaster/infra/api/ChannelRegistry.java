package dev.github.j4c62.broadcaster.infra.api;

import dev.github.j4c62.broadcaster.infra.module.ChannelModule;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ChannelRegistry {
    private static final Map<String, ChannelModule.ChannelHandler> registry = new HashMap<>();

    public static boolean isRegistered(Set<String> channels) {
        return registry.keySet().containsAll(channels);
    }

    public static void register(String channels, ChannelModule.ChannelHandler handler) {
        registry.put(channels, handler);
    }

    public static ChannelModule.ChannelHandler get(String channels) {
        return registry.getOrDefault(channels, null);
    }

}
