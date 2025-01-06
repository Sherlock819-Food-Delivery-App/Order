package com.example.Order.service;

import com.example.Order.model.Order;
import com.example.Order.model.OrderItem;
import com.example.Order.repository.OrderRepository;
import com.example.Order.service.event.OrderEventPublisher;
import org.example.enums.OrderStatus;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderEventPublisher eventPublisher;
    @Autowired
    private CartService cartService;

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY = 1000L;

    @Override
    public Order createOrder(Order order) {
        // Existing createOrder logic remains the same
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }

        for (OrderItem orderItem : order.getOrderItems()) {
            orderItem.setOrder(order);
        }
        Order createdOrder = orderRepository.save(order);
        eventPublisher.publishOrderUpdate(createdOrder, createdOrder.getRestaurantId().toString());

        // Clear the cart
        cartService.clearCart(order.getEmail());

        return createdOrder;
    }

    @Override
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public List<Order> getOrderByEmail(String email) {
        return orderRepository.findByEmail(email);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    @Transactional
    public Optional<Order> updateOrder(Long orderId, Order updatedOrder) {
        int retryCount = 0;
        while (retryCount < MAX_RETRY_ATTEMPTS) {
            try {
                int finalRetryCount = retryCount;
                return orderRepository.findById(orderId)
                        .map(existingOrder -> {
                            existingOrder.updateFromOrder(updatedOrder);
                            Order saved = orderRepository.save(existingOrder);
                            log.info("Order {} updated successfully on attempt {}", orderId, finalRetryCount + 1);
                            return Optional.of(saved);
                        })
                        .orElse(Optional.empty());
            } catch (ObjectOptimisticLockingFailureException | StaleObjectStateException e) {
                retryCount++;
                if (retryCount >= MAX_RETRY_ATTEMPTS) {
                    log.error("Failed to update order {} after {} attempts", orderId, MAX_RETRY_ATTEMPTS);
                    throw new RuntimeException("Failed to update order after maximum retry attempts", e);
                }
                log.warn("Concurrent modification detected for order {}. Retry attempt {}", orderId, retryCount);
                try {
                    Thread.sleep(RETRY_DELAY);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Update was interrupted during retry delay", ie);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteOrder(Long orderId) {
        if (orderRepository.existsById(orderId)) {
            orderRepository.deleteById(orderId);
            return true;
        }
        return false;
    }
}