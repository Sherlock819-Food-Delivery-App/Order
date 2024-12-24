package com.example.Order.service.rabbitmq;

import com.example.Order.dto.OrderDTO;
import com.example.Order.service.OrderService;
import com.example.Order.utilities.OrderMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class OrderListener {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void startListening(Channel channel, String requestQueueName, String responseRoutingKey) throws Exception {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            OrderDTO orderDTO = objectMapper.readValue(message, OrderDTO.class);

            System.out.println("ORDER STATUS: " + orderDTO.getStatus());

            // Simulate order processing - Will need to respond to connected Client through WebSocket
            Map<String, Object> order = new HashMap<>();
            order.put("orderId", orderDTO.getOrderId());
            order.put("status", orderDTO.getStatus());
            messagingTemplate.convertAndSend("/topic/orderDetails/" + orderDTO.getOrderId(), objectMapper.writeValueAsString(order));

            orderService.updateOrder(orderDTO.getOrderId(), orderMapper.toEntity(orderDTO));
        };

        channel.basicConsume(requestQueueName, true, deliverCallback, consumerTag -> {
        });
    }
}
