package com.github.j4c62.broadcaster.exception;

import com.github.j4c62.broadcaster.BroadcastEvent;

/**
 * Represents an error that occurred during the broadcasting process, including the context of the
 * delivery attempt if available.
 *
 * @param event The broadcast event being processed when the error occurred (can be null).
 * @param error The exception thrown during delivery.
 */
public record BroadcastErrorEvent(BroadcastEvent event, Throwable error) {}
