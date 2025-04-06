package dev.github.j4c62.broadcaster.core.deliverer;

import dev.github.j4c62.broadcaster.core.data.Channel;
import dev.github.j4c62.broadcaster.core.dto.Notification;

import static dev.github.j4c62.broadcaster.core.data.Channel.EMAIL;

public class EmailDeliverer implements Deliverer {


    @Override
    public void deliver(Notification notification) {
        System.out.println("Delivering..." + notification);
    }

    @Override
    public Channel channel() {
        return EMAIL;
    }
}
