package com.jaimebrolesi.springamqpexample.configuration;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@EnableJms
@Configuration
public class JmsConfig {

    @Value("${jms.broker-url}")
    private String brokerUrl;

    @Value("${jms.username}")
    private String username;

    @Value("${jms.password}")
    private String password;

    private ActiveMQConnectionFactory activeMQConnectionFactory() {
        final ActiveMQConnectionFactory activeMQConnectionFactory =
                new ActiveMQConnectionFactory(username, password, brokerUrl);
        activeMQConnectionFactory.setPrefetchPolicy(activeMQPrefetchPolicy());
        return activeMQConnectionFactory;
    }

    private ActiveMQPrefetchPolicy activeMQPrefetchPolicy() {
        ActiveMQPrefetchPolicy activeMQPrefetchPolicy = new ActiveMQPrefetchPolicy();
        activeMQPrefetchPolicy.setQueuePrefetch(1000);
        return activeMQPrefetchPolicy;
    }

    @Bean
    public PooledConnectionFactory pooledConnectionFactory() {
        final PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setConnectionFactory(activeMQConnectionFactory());
        pooledConnectionFactory.setMaxConnections(10);
        return pooledConnectionFactory;

    }

    @Bean
    public JmsListenerContainerFactory<?> jmsFactoryQueue() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConcurrency("10");
        factory.setMessageConverter(messageConverter());
        return factory;
    }

    @Bean
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    @Bean
    public JmsTemplate queueJmsTemplate() {
        return new JmsTemplate(pooledConnectionFactory());
    }

    @Bean
    public JmsTemplate topicJmsTemplate() {
        final JmsTemplate jmsTemplate = new JmsTemplate(activeMQConnectionFactory());
        jmsTemplate.setPubSubDomain(true);
        return jmsTemplate;
    }

}
