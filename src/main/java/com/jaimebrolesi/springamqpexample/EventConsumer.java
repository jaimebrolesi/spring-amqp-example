package com.jaimebrolesi.springamqpexample;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {

    @RabbitListener(queues="eventQueue")
    public void receive(String message) {
        System.out.println(message);
    }
}
