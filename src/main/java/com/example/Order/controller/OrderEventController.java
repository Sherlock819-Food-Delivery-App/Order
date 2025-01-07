package com.example.Order.controller;

import com.example.Order.service.OrderEventService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/order-events")
public class OrderEventController {

    private final OrderEventService orderEventService;

    public OrderEventController(OrderEventService orderEventService) {
        this.orderEventService = orderEventService;
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderEventService.createEmitter(email);
    }
} 