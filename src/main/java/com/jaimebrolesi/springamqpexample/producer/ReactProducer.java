package com.jaimebrolesi.springamqpexample.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.OutboundMessageResult;
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
        Flux<OutboundMessageResult> confirmations = sender.sendWithPublishConfirms(Flux.interval(Duration.ofMillis(1))
                .map(i -> new OutboundMessage("", queue, ("Message_" + i).getBytes())));
        disposable = sender.declareQueue(QueueSpecification.queue(queue))
                        .thenMany(confirmations)
                        .doOnError(e -> LOGGER.error("Send failed", e))
                        .subscribe(r -> {
                            if (r.isAck()) {
                                LOGGER.info("Message {} sent successfully",
                                        new String(r.getOutboundMessage().getBody()));
                            }
                        });
    }

    @PreDestroy
    private void destroy() {
        disposable.dispose();
        sender.close();
    }
}
