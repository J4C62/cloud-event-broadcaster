package com.github.j4c62.broadcaster;

import com.github.j4c62.delivery.Deliverer;
import com.github.j4c62.delivery.Diffusible;

/** A record class that encapsulates the event of delivering a notification. */
public record BroadcastEvent(Deliverer deliverer, Diffusible notification) {}
