package com.example.Order.dto;

import com.example.Order.model.Order;
import lombok.Data;

@Data
public class OrderUpdateEvent {
    private final Order order;
    private final String restaurantId;

    public OrderUpdateEvent(Order order, String restaurantId) {
        this.order = order;
        this.restaurantId = restaurantId;
    }
}
