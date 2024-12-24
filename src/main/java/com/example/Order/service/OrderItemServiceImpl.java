package com.example.Order.service;

import com.example.Order.model.OrderItem;
import com.example.Order.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public OrderItem createOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    @Override
    public Optional<OrderItem> getOrderItemById(Long orderItemId) {
        return orderItemRepository.findById(orderItemId);
    }

    @Override
    public List<OrderItem> getAllOrderItems() {
        return orderItemRepository.findAll();
    }

    @Override
    public Optional<OrderItem> updateOrderItem(Long orderItemId, OrderItem orderItem) {
        if (orderItemRepository.existsById(orderItemId)) {
            orderItem.setOrderItemId(orderItemId); // Set the existing order item ID
            return Optional.of(orderItemRepository.save(orderItem));
        }
        return Optional.empty(); // Order item not found
    }

    @Override
    public boolean deleteOrderItem(Long orderItemId) {
        if (orderItemRepository.existsById(orderItemId)) {
            orderItemRepository.deleteById(orderItemId);
            return true; // Successfully deleted
        }
        return false; // Order item not found
    }
}
