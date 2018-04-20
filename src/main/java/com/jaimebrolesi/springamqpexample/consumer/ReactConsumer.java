package com.jaimebrolesi.springamqpexample.consumer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Delivery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.Sender;

import javax.annotation.PreDestroy;

@Service
public class ReactConsumer {

    private static final String QUEUE = "demo-queue";
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactConsumer.class);

    private final Receiver receiver;
    private final Sender sender;
    private Disposable disposable;

    public ReactConsumer(Receiver receiver, Sender sender) {
        this.receiver = receiver;
        this.sender = sender;
    }

    public void consume(String queue) {
        Mono<AMQP.Queue.DeclareOk> queueDeclaration = sender.declareQueue(QueueSpecification.queue(queue));
        Flux<Delivery> messages = receiver.consumeAutoAck(queue);
        disposable = queueDeclaration.thenMany(messages).subscribe(m -> {
            LOGGER.info("Received message {}", new String(m.getBody()));
        });
    }

    @PreDestroy
    public void destroy() {
        this.disposable.dispose();
        this.sender.close();
        this.receiver.close();
    }
}
