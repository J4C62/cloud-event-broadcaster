package dev.github.j4c62.broadcaster.infra.listener;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import dev.github.j4c62.broadcaster.core.trigger.TriggerBroadcast;
import dev.github.j4c62.broadcaster.infra.delivery.dto.CloudEvent;
import dev.github.j4c62.broadcaster.infra.module.DomainModule;
import dev.github.j4c62.broadcaster.infra.module.RabbitMQModule;
import java.nio.charset.StandardCharsets;

public class RabbitMQListener {

  private final Channel channel;
  private final TriggerBroadcast triggerBroadcast;

  @Inject
  public RabbitMQListener(
      @Named("rabbitmqChannel") Channel channel,
      @Named("Trigger") TriggerBroadcast triggerBroadcast) {
    this.channel = channel;
    this.triggerBroadcast = triggerBroadcast;
    Injector injector = Guice.createInjector(new RabbitMQModule(), new DomainModule());
    injector.injectMembers(this);
  }

  public void startListening(String queueName) throws Exception {
    channel.queueDeclare(queueName, false, false, false, null);

    DeliverCallback deliverCallback =
        (String v, Delivery delivery) -> {
          var message = new String(delivery.getBody(), StandardCharsets.UTF_8);
          var mapper = new ObjectMapper();
          mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
          var jsonNode = mapper.readTree(message);

          var cloudEvent = mapper.treeToValue(jsonNode, CloudEvent.class);

          triggerBroadcast.trigger(cloudEvent);
        };

    channel.basicConsume(queueName, true, deliverCallback, v -> {});
  }
}
