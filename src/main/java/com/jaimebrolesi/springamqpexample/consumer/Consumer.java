package com.jaimebrolesi.springamqpexample.consumer;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class Consumer {

//    @JmsListener(destination = "eventQueue")
//    public void receive(String message) {
//        System.out.println("Received message: " + message + " | " + new Date());
//    }
}
