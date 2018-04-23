package com.jaimebrolesi.springamqpexample.producer;

import com.rabbitmq.client.AMQP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.rabbitmq.ExchangeSpecification;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.OutboundMessageResult;
import reactor.rabbitmq.Sender;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ReactExchangeProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactExchangeProducer.class);

    private final Sender sender;

    private Disposable disposable;

    private static final String EXCHANGE_ARGS_TYPE = "x-delayed-type";
    private static final String EXCHANGE_ARGS_TOPIC = "topic";
    private static final String EXCHANGE_TYPE = "x-delayed-message";

    public ReactExchangeProducer(Sender sender) {
        this.sender = sender;
    }

    public void produce(String exchange) {

        Flux<OutboundMessageResult> confirmations = sender.sendWithPublishConfirms(Flux.interval(Duration.ofMillis(1))
                .map(i -> new OutboundMessage(exchange, "producer.create", basicProperties(), ("Message_" + i)
                        .getBytes())));

        disposable = sender.declareExchange(exchangeSpecification(exchange))
                        .thenMany(confirmations)
                        .doOnError(e -> LOGGER.error("Send failed", e))
                        .subscribe();
    }

    private AMQP.BasicProperties basicProperties() {
        AMQP.BasicProperties.Builder props = new AMQP.BasicProperties.Builder();
        Map<String, Object> headers = new HashMap<>();
        headers.put("x-delay", TimeUnit.SECONDS.toMillis(1));
        props.headers(headers);
        return props.build();
    }

    private ExchangeSpecification exchangeSpecification(String exchange) {
        Map<String, Object> args = new HashMap<>();
        args.put(EXCHANGE_ARGS_TYPE, EXCHANGE_ARGS_TOPIC);
        return ExchangeSpecification.exchange(exchange).type(EXCHANGE_TYPE).durable(true).arguments(args);
    }

    @PreDestroy
    private void destroy() {
        disposable.dispose();
        sender.close();
    }
}

//.subscribe(r -> {
//        if (r.isAck()) {
//        LOGGER.info("Message {} sent successfully",
//        new String(r.getOutboundMessage().getBody()));
//        }
//        });
