package com.jaimebrolesi.springamqpexample.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Sender;

import javax.annotation.PreDestroy;
import java.time.Duration;

@Service
public class ReactProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactProducer.class);

    private final Sender sender;

    private Disposable disposable;

    public ReactProducer(Sender sender) {
        this.sender = sender;
    }

    public void send(String queue) {
        Mono<Void> confirmations = sender.send(Flux.interval(Duration.ofDays(1L))
                .map(i -> new OutboundMessage("", queue, ("Message_" + i).getBytes())));
        disposable = sender.declareQueue(QueueSpecification.queue(queue))
                        .thenMany(confirmations)
                        .doOnError(e -> LOGGER.error("Send failed", e))
                        .subscribe();
    }

    @PreDestroy
    private void destroy() {
        disposable.dispose();
        sender.close();
    }
}
