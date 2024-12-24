package com.example.Order.service;

import com.example.Order.model.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderItemService {
    OrderItem createOrderItem(OrderItem orderItem);

    Optional<OrderItem> getOrderItemById(Long orderItemId);

    List<OrderItem> getAllOrderItems();

    Optional<OrderItem> updateOrderItem(Long orderItemId, OrderItem orderItem);

    boolean deleteOrderItem(Long orderItemId);
}
