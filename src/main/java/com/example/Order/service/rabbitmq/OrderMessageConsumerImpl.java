//package com.example.Order.service.rabbitmq;
//
//import com.example.Order.dto.OrderDTO;
//import com.example.Order.model.Order;
//import com.example.Order.service.OrderService;
//import com.example.Order.service.event.OrderEventPublisher;
//import com.example.Order.utilities.OrderMapper;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.rabbitmq.client.Channel;
//import jakarta.annotation.PreDestroy;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.core.*;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitAdmin;
//import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
//import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.MessagingException;
//import org.springframework.stereotype.Service;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Map;
//import java.util.Properties;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Service
//@Slf4j
//public class OrderMessageConsumerImpl implements OrderMessageConsumer {
//
//    private final RabbitAdmin rabbitAdmin;
//    private final ConnectionFactory connectionFactory;
//    private final TopicExchange orderExchange;
//    private final Map<String, SimpleMessageListenerContainer> containers = new ConcurrentHashMap<>();
//
//    private final OrderService orderService;
//
//    private final ObjectMapper objectMapper;
//
//    @Autowired
//    private OrderMapper orderMapper;
//
//    @Autowired
//    private OrderEventPublisher eventPublisher;
//
//    @Autowired
//    public OrderMessageConsumerImpl(RabbitAdmin rabbitAdmin,
//                                    ConnectionFactory connectionFactory,
//                                    TopicExchange orderExchange,
//                                    ObjectMapper objectMapper, OrderService orderService) {
//        this.orderService = orderService;
//        this.rabbitAdmin = rabbitAdmin;
//        this.connectionFactory = connectionFactory;
//        this.orderExchange = orderExchange;
//        this.objectMapper = objectMapper;
//    }
//
//    @Override
//    public void listenToRestaurantResponseQueue(String restaurantId) {
//        String responseQueueName = "restaurant_" + restaurantId + "_responseQueue";
//        String routingKey = "restaurant." + restaurantId + ".response";
//
//        // Check if we're already listening to this restaurant's queue
//        synchronized (this) {
//            if (!containers.containsKey(restaurantId)) {
//                // Create queue and binding if they don't exist
//                createQueueAndBinding(responseQueueName, routingKey);
//
//                // Set up listener for the response queue
//                setupListener(responseQueueName, restaurantId);
//
//                log.info("Started listening to response queue for restaurant: {}", restaurantId);
//            }
//        }
//    }
//
//    private void createQueueAndBinding(String responseQueueName, String routingKey) {
//        try {
//            Properties queueProperties = rabbitAdmin.getQueueProperties(responseQueueName);
//            if (queueProperties == null) {
//                Queue responseQueue = new Queue(responseQueueName, true);
//                rabbitAdmin.declareQueue(responseQueue);
//
//                Binding responseBinding = BindingBuilder.bind(responseQueue)
//                        .to(orderExchange)
//                        .with(routingKey);
//                rabbitAdmin.declareBinding(responseBinding);
//
//                log.info("Created new queue and binding: {}", responseQueueName);
//            }
//        } catch (Exception e) {
//            log.error("Error creating response queue: {}", responseQueueName, e);
//            throw new MessagingException("Failed to create response queue", e);
//        }
//    }
//
//    private void setupListener(String responseQueueName, String restaurantId) {
//        try {
//            SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//            container.setConnectionFactory(connectionFactory);
//            container.setQueueNames(responseQueueName);
//            container.setMessageListener(new MessageListenerAdapter(new ResponseMessageHandler(restaurantId, objectMapper, orderService, eventPublisher, orderMapper)) {
//                @Override
//                public void onMessage(Message message, Channel channel) {
//                    try {
//                        new ResponseMessageHandler(restaurantId, objectMapper, orderService, eventPublisher, orderMapper).handleMessage(new String(message.getBody(), StandardCharsets.UTF_8));
//                        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//                    } catch (Exception e) {
//                        log.error("Error in message processing for restaurant {}: {}", restaurantId, e);
//                    }
//                }
//            });
//            container.start();
//
//            containers.put(restaurantId, container);
//        } catch (Exception e) {
//            log.error("Error setting up listener for restaurant: {}", restaurantId, e);
//            throw new MessagingException("Failed to set up message listener", e);
//        }
//    }
//
//    @PreDestroy
//    public void stopAllListeners() {
//        containers.forEach((restaurantId, container) -> {
//            try {
//                container.stop();
//                log.info("Stopped listener for restaurant: {}", restaurantId);
//            } catch (Exception e) {
//                log.error("Error stopping listener for restaurant: {}", restaurantId, e);
//            }
//        });
//        containers.clear();
//    }
//
//    // Separate class for message handling
//    private static class ResponseMessageHandler {
//        private final String restaurantId;
//        private final ObjectMapper objectMapper;
//        private final OrderService orderService;
//        private final OrderMapper orderMapper;
//        private final OrderEventPublisher eventPublisher;
//
//        public ResponseMessageHandler(String restaurantId, ObjectMapper objectMapper, OrderService orderService, OrderEventPublisher eventPublisher, OrderMapper orderMapper) {
//            this.eventPublisher = eventPublisher;
//            this.orderService = orderService;
//            this.restaurantId = restaurantId;
//            this.objectMapper = objectMapper;
//            this.orderMapper = orderMapper;
//        }
//
//        @SuppressWarnings("unused")
//        public void handleMessage(String responseMessage) throws JsonProcessingException {
//            log.info("Received response from restaurant {}: {}", restaurantId, responseMessage);
//            // Process the response as needed
//            // You might want to add your business logic here
//            Order order = orderMapper.toEntity(objectMapper.readValue(responseMessage, OrderDTO.class));
//            orderService.updateOrder(order.getOrderId(), order);
//            System.out.println("Order updated successfully: " + order);
//            if(!order.getStatus().equals("DELIVERED"))
//                eventPublisher.publishOrderUpdate(order, restaurantId);
//        }
//    }
//
//    // Optional: Method to stop listening to a specific restaurant's queue
//    public void stopListeningToRestaurant(String restaurantId) {
//        SimpleMessageListenerContainer container = containers.remove(restaurantId);
//        if (container != null) {
//            container.stop();
//            log.info("Stopped listening to restaurant: {}", restaurantId);
//        }
//    }
//}