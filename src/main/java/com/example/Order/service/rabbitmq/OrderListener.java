package com.example.Order.service.rabbitmq;

import com.example.Order.dto.OrderDTO;
import com.example.Order.service.OrderEventService;
import com.example.Order.service.OrderService;
import com.example.Order.utilities.OrderMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.beans.factory.annotation.Autowired;
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
    private OrderEventService orderEventService;

    public void startListening(Channel channel, String requestQueueName, String responseRoutingKey) throws Exception {
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            OrderDTO orderDTO = objectMapper.readValue(message, OrderDTO.class);

            // Create order update message
            Map<String, Object> orderUpdate = new HashMap<>();
            orderUpdate.put("orderId", orderDTO.getOrderId());
            orderUpdate.put("status", orderDTO.getStatus());

            // Send SSE update to the specific user
            orderEventService.sendOrderUpdate(orderDTO.getEmail(), orderUpdate);

            orderService.updateOrder(orderDTO.getOrderId(), orderMapper.toEntity(orderDTO));
        };

        channel.basicConsume(requestQueueName, true, deliverCallback, consumerTag -> {});
    }
}
