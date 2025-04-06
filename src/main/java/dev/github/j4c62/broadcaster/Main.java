package dev.github.j4c62.broadcaster;


import com.google.inject.Guice;
import com.google.inject.Injector;
import dev.github.j4c62.broadcaster.infra.listener.RabbitMQListener;
import dev.github.j4c62.broadcaster.infra.module.RabbitMQModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) {

        Injector injector = Guice.createInjector(new RabbitMQModule());

        RabbitMQListener listener = injector.getInstance(RabbitMQListener.class);

        try {
            listener.startListening("testQueue");
        } catch (Exception e) {
            log.error("e: ", e);
        }
    }

}
