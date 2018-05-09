package com.jaimebrolesi.springamqpexample.producer;

import org.apache.activemq.ScheduledMessage;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.jms.JMSException;
import javax.jms.Message;
import java.text.MessageFormat;
import java.time.*;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class Producer {

    @Autowired
    private JmsTemplate queueJmsTemplate;

    public void send(String queueName, String message, LocalDateTime time) {

        ExecutorService exec = Executors.newFixedThreadPool(160);
        final Duration duration = Duration.between(time, time.plusSeconds(10));
        try {
            for (final Integer o : createInsertionPolicy()) {
                exec.submit(() -> {
                    final String messageFormatted = MessageFormat.format(message, o, duration.toMillis());
                    final AtomicReference<Message> msg = new AtomicReference<>();
                    queueJmsTemplate.convertAndSend(queueName, messageFormatted, delayed -> {
                        delayed.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, duration.toMillis());
                        msg.set(delayed);
                        return delayed;

                    });
                });
            }
        } finally {
            exec.shutdown();
            try {
                exec.awaitTermination(1L, TimeUnit.SECONDS);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private LinkedList<Integer> createInsertionPolicy() {
        LinkedList<Integer> elements = new LinkedList<>();
        //31 * 5 = 155 =
        for (int i = 0; i < 31*5; ++i) {
            elements.add(i);
        }
        return elements;
    }
}
