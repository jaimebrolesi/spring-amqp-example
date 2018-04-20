package com.jaimebrolesi.springamqpexample;

import com.jaimebrolesi.springamqpexample.consumer.ReactConsumer;
import com.jaimebrolesi.springamqpexample.producer.ReactProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import reactor.core.Disposable;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class SpringAmqpExampleApplication {

    private static final String QUEUE = "demo-queue";

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SpringAmqpExampleApplication.class, args);
        final ReactProducer producer = context.getBean(ReactProducer.class);
        final ReactConsumer consumer = context.getBean(ReactConsumer.class);

        producer.send(QUEUE);
        consumer.consume(QUEUE);
	}
}
