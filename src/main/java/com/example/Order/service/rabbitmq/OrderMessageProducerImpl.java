//package com.example.Order.service.rabbitmq;
//
//import com.example.Order.config.rabbitmq.RabbitMQConfig;
//import com.example.Order.dto.OrderUpdateEvent;
//import com.example.Order.service.MessageService;
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.core.TopicExchange;
//import org.springframework.amqp.rabbit.core.RabbitAdmin;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Service;
//
//import java.util.Properties;
//
//@Service
//public class OrderMessageProducerImpl implements MessageService, OrderMessageProducer {
//
//    private final RabbitTemplate rabbitTemplate;
//    private final RabbitAdmin rabbitAdmin;
//    private final TopicExchange orderExchange;
//
//    @Autowired
//    private OrderMessageConsumer orderMessageConsumer;
//
//    @Autowired
//    public OrderMessageProducerImpl(RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin, TopicExchange orderExchange) {
//        this.rabbitTemplate = rabbitTemplate;
//        this.rabbitAdmin = rabbitAdmin;
//        this.orderExchange = orderExchange;
//    }
//
//    public void sendOrderMessage(String restaurantId, String orderMessage) {
//        String requestQueueName = "restaurant_" + restaurantId + "_requestQueue";
//        String routingKey = "restaurant." + restaurantId + ".request";
//
//        // Check if queue exists
//        Properties queueProperties = rabbitAdmin.getQueueProperties(requestQueueName);
//
//        // Create queue and binding if it doesn't exist
//        if (queueProperties == null) {
//            Queue requestQueue = new Queue(requestQueueName, true);
//            rabbitAdmin.declareQueue(requestQueue);
//
//            Binding requestBinding = BindingBuilder.bind(requestQueue)
//                    .to(orderExchange)
//                    .with(routingKey);
//            rabbitAdmin.declareBinding(requestBinding);
//        }
//
//        rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, routingKey, orderMessage);
//        orderMessageConsumer.listenToRestaurantResponseQueue(restaurantId);
//    }
//
//    @EventListener
//    public void handleOrderUpdate(OrderUpdateEvent event) {
//        sendMessage(event.getRestaurantId(), event.getOrder().getJson());
//    }
//
//    @Override
//    public void sendMessage(String restaurantId, String message) {
//        sendOrderMessage(restaurantId, message);
//    }
//}
