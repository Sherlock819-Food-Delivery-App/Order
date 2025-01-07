package com.example.Order.controller;

import com.example.Order.dto.OrderDTO;
import com.example.Order.model.Order;
import com.example.Order.service.CartService;
import com.example.Order.service.OrderService;
import com.example.Order.service.OrderServiceImpl;
import com.example.Order.utilities.OrderMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderServiceImpl;
    private final OrderMapper orderMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CartService cartService;

    @Autowired
    public OrderController(OrderServiceImpl orderServiceImpl, OrderMapper orderMapper) {
        this.orderServiceImpl = orderServiceImpl;
        this.orderMapper = orderMapper;
    }

//    @PostMapping
//    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
//        log.info("Creating order for user: {}", SecurityContextHolder.getContext().getAuthentication().getName());
//        orderDTO.setEmail(SecurityContextHolder.getContext().getAuthentication().getName());
//        var order = orderMapper.toEntity(orderDTO);
//        var createdOrder = orderServiceImpl.createOrder(order);
//        return new ResponseEntity<>(orderMapper.toDTO(createdOrder), HttpStatus.CREATED);
//    }

    @PostMapping
    public ResponseEntity<OrderDTO> placeOrder() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Creating order for user: {}", email);
        Order order = cartService.convertCartToOrder(email);
        var createdOrder = orderServiceImpl.createOrder(order);
        return new ResponseEntity<>(orderMapper.toDTO(createdOrder), HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        var order = orderServiceImpl.getOrderById(orderId);
        return order.isPresent() ?
                ResponseEntity.ok(orderMapper.toDTO(order.orElse(null))) :
                ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        var orders = orderServiceImpl.getAllOrders();
        return ResponseEntity.ok(orderMapper.toDTO(orders));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long orderId, @RequestBody OrderDTO orderDTO) {
        orderDTO.setEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        var order = orderMapper.toEntity(orderDTO);
        var updatedOrder = orderServiceImpl.updateOrder(orderId, order);
        return updatedOrder.isPresent() ?
                ResponseEntity.ok(orderMapper.toDTO(updatedOrder.orElse(null))) :
                ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId) {
        if (orderServiceImpl.deleteOrder(orderId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
