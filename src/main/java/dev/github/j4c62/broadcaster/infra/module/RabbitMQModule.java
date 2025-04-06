package dev.github.j4c62.broadcaster.infra.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQModule extends AbstractModule {

    @Provides
    @Named("rabbitmqConnection")
    @Singleton
    @SuppressWarnings("unused")
    public Connection provideConnection() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        return factory.newConnection();
    }

    @Provides
    @Named("rabbitmqChannel")
    @Singleton
    @SuppressWarnings("unused")
    public Channel provideChannel(@Named("rabbitmqConnection") Connection connection) throws Exception {
        return connection.createChannel();
    }
}
