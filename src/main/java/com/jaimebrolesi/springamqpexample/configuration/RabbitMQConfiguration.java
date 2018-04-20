package com.jaimebrolesi.springamqpexample.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.rabbitmq.*;

@Configuration
public class RabbitMQConfiguration {
    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private Integer port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername(this.username);
        connectionFactory.setPassword(this.password);
        connectionFactory.setHost(this.host);
        connectionFactory.setPort(this.port);
        connectionFactory.useNio();
        return connectionFactory;
    }

    @Bean
    public Receiver receiver(ConnectionFactory connectionFactory) {
        ReceiverOptions options = new ReceiverOptions();
        options.connectionFactory(connectionFactory);
        return ReactorRabbitMq.createReceiver(options);
    }

    @Bean
    public Sender sender(ConnectionFactory connectionFactory) {
        SenderOptions options = new SenderOptions();
        options.connectionFactory(connectionFactory);
        return ReactorRabbitMq.createSender(options);
    }

    @Bean
    public ObjectMapper mapper() {
        return new ObjectMapper();
    }

}
