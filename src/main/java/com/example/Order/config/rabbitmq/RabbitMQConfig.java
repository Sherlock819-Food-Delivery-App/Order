package com.example.Order.config.rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

//    public static final String ORDER_EXCHANGE = "orderExchange";
//
//    @Bean
//    public TopicExchange orderExchange() {
//        return new TopicExchange(ORDER_EXCHANGE);
//    }
//
//    @Bean
//    public ConnectionFactory connectionFactory() {
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost"); // replace with your RabbitMQ server address
//        connectionFactory.setPort(5672); // default RabbitMQ port
//        return connectionFactory;
//    }
//
//    @Bean
//    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
//        return new RabbitAdmin(connectionFactory);
//    }

    @Value("${spring.rabbitmq.host}")
    private String host;

    public static final String ORDER_EXCHANGE = "orderExchange";

    @Bean
    public Connection rabbitMQConnection() throws Exception {
        com.rabbitmq.client.ConnectionFactory factory = new ConnectionFactory();
        log.info("RabbitMQ host: {}", host);
        factory.setHost(host);
        return factory.newConnection();
    }
}
