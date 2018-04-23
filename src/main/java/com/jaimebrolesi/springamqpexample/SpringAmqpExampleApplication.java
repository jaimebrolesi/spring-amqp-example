package com.jaimebrolesi.springamqpexample;

import com.jaimebrolesi.springamqpexample.consumer.ReactConsumer;
import com.jaimebrolesi.springamqpexample.producer.ReactExchangeProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringAmqpExampleApplication {

    private static final String QUEUE = "eventQueue";
    private static final String EXCHANGE = "delayedExchange";

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SpringAmqpExampleApplication.class, args);
        //final ReactProducer producer = context.getBean(ReactProducer.class);
        final ReactExchangeProducer producer = context.getBean(ReactExchangeProducer.class);
        final ReactConsumer consumer = context.getBean(ReactConsumer.class);

        //producer.produce(EXCHANGE);
        consumer.consume(QUEUE);
	}
}
