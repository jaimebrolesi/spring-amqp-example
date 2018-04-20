package com.jaimebrolesi.springamqpexample;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class EventProducer {

    private final RabbitTemplate rabbitTemplate;

    private final Exchange exchange;

    public EventProducer(RabbitTemplate rabbitTemplate, Exchange exchange) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    public void createEvent() {
        String routingKey = "producer.created";
        String message = "producer created";
        rabbitTemplate.convertAndSend(exchange.getName(), routingKey, message, intercept -> {
            intercept.getMessageProperties().setDelay(3000);
            return intercept;
        });
    }

}
