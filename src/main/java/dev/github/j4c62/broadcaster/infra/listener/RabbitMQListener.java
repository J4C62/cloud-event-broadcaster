package dev.github.j4c62.broadcaster.infra.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import dev.github.j4c62.broadcaster.core.dto.CloudEvent;

import java.nio.charset.StandardCharsets;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public class RabbitMQListener {

    private final Channel channel;

    @Inject
    public RabbitMQListener(@Named("rabbitmqChannel") Channel channel) {
        this.channel = channel;
    }

    public void startListening(String queueName) throws Exception {
        channel.queueDeclare(queueName, false, false, false, null);

        DeliverCallback deliverCallback = (String _, Delivery delivery) -> {
            var message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            var mapper = new ObjectMapper();
            mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

            var jsonNode = mapper.readTree(message);
            var cloudEvent = mapper.treeToValue(jsonNode, CloudEvent.class);

            System.out.println(cloudEvent);
        };

        channel.basicConsume(queueName, true, deliverCallback, _ -> {
        });
    }
}
