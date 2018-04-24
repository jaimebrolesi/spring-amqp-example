package com.jaimebrolesi.springamqpexample;

import com.jaimebrolesi.springamqpexample.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringAmqpExampleApplication {

    private static final String QUEUE = "eventQueue";
    private static final String EXCHANGE = "delayedExchange";

    public static void main(String[] args) {
        SpringApplication.run(SpringAmqpExampleApplication.class, args);
    }

    @Autowired
    private Producer producer;

    @RequestMapping(method = RequestMethod.GET, value = "/send")
    private void send() {
        producer.send(QUEUE, "This message was delayed.");
    }
}
