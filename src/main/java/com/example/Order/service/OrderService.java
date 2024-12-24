package com.example.Order.service;

import com.example.Order.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Order order);

    Optional<Order> getOrderById(Long orderId);

    List<Order> getOrderByEmail(String email);

    List<Order> getAllOrders();

    Optional<Order> updateOrder(Long orderId, Order order);

    boolean deleteOrder(Long orderId);
}
