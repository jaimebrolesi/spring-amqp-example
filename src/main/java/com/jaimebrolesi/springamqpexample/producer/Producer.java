package com.jaimebrolesi.springamqpexample.producer;

import org.apache.activemq.ScheduledMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class Producer {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void send(String queueName, String message) {
        jmsTemplate.convertAndSend(queueName, message, message1 -> {
            System.out.println("Message sent at " + new Date());
            message1.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,5000);
            return message1;
        });
    }


}
