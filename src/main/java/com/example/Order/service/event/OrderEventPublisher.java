package com.example.Order.service.event;

import com.example.Order.dto.OrderUpdateEvent;
import com.example.Order.model.Order;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class OrderEventPublisher {
    private final ApplicationEventPublisher publisher;

    public OrderEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishOrderUpdate(Order order, String restaurantId) {
        publisher.publishEvent(new OrderUpdateEvent(order, restaurantId));
    }
}
