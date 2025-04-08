package com.github.j4c62.broadcaster.spec;

/**
 * Interface combining both {@link BroadcasterDeliveryConfigurator} and {@link
 * BroadcasterExecutionConfigurator}. This interface provides methods for configuring both the
 * delivery filter and the execution logic of broadcasting.
 */
public interface BroadcasterSpec
    extends BroadcasterDeliveryConfigurator, BroadcasterExecutionConfigurator {}
