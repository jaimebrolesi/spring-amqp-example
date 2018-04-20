package com.jaimebrolesi.springamqpexample;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProducerConfiguration {

    @Bean
    public TopicExchange eventExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        TopicExchange delayedExchange = new TopicExchange("delayedExchange", true, false, args);
        delayedExchange.setDelayed(true);
        return delayedExchange;
    }

    @Bean
    public EventProducer ProducerService(RabbitTemplate rabbitTemplate, Exchange eventExchange) {
        return new EventProducer(rabbitTemplate, eventExchange);
    }
}
