package com.example.Order.controller;

import com.example.Order.dto.OrderItemDTO;
import com.example.Order.model.OrderItem;
import com.example.Order.service.OrderItemService;
import com.example.Order.service.OrderItemServiceImpl;
import com.example.Order.utilities.OrderItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    private final OrderItemService orderItemServiceImpl;
    private final OrderItemMapper orderItemMapper;

    @Autowired
    public OrderItemController(OrderItemServiceImpl orderItemServiceImpl, OrderItemMapper orderItemMapper) {
        this.orderItemServiceImpl = orderItemServiceImpl;
        this.orderItemMapper = orderItemMapper;
    }

    @PostMapping
    public ResponseEntity<OrderItemDTO> createOrderItem(@RequestBody OrderItemDTO orderItemDTO) {
        var orderItem = orderItemMapper.toEntity(orderItemDTO);
        var createdOrderItem = orderItemServiceImpl.createOrderItem(orderItem);
        return new ResponseEntity<>(orderItemMapper.toDTO(createdOrderItem), HttpStatus.CREATED);
    }

    @GetMapping("/{orderItemId}")
    public ResponseEntity<OrderItemDTO> getOrderItemById(@PathVariable Long orderItemId) {
        Optional<OrderItem> orderItem = orderItemServiceImpl.getOrderItemById(orderItemId);
        return orderItem != null ?
                ResponseEntity.ok(orderItemMapper.toDTO(orderItem.orElse(null))) :
                ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<OrderItemDTO>> getAllOrderItems() {
        var orderItems = orderItemServiceImpl.getAllOrderItems();
        return ResponseEntity.ok(orderItemMapper.toDTO(orderItems));
    }

    @PutMapping("/{orderItemId}")
    public ResponseEntity<OrderItemDTO> updateOrderItem(@PathVariable Long orderItemId, @RequestBody OrderItemDTO orderItemDTO) {
        var orderItem = orderItemMapper.toEntity(orderItemDTO);
        var updatedOrderItem = orderItemServiceImpl.updateOrderItem(orderItemId, orderItem);
        return updatedOrderItem.isPresent() ?
                ResponseEntity.ok(orderItemMapper.toDTO(updatedOrderItem.orElse(null))) :
                ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{orderItemId}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long orderItemId) {
        if (orderItemServiceImpl.deleteOrderItem(orderItemId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
