package com.example.Order.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderEventService {
    private static final Logger log = LoggerFactory.getLogger(OrderEventService.class);
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(String email) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        emitter.onCompletion(() -> emitters.remove(email));
        emitter.onTimeout(() -> emitters.remove(email));
        emitter.onError(e -> emitters.remove(email));
        
        emitters.put(email, emitter);
        
        // Send initial connection established event
        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("Connected"));
        } catch (IOException e) {
            emitters.remove(email);
        }
        
        return emitter;
    }

    public void sendOrderUpdate(String email, Object orderUpdate) {
        SseEmitter emitter = emitters.get(email);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("ORDER_UPDATE")
                        .data(orderUpdate));
            } catch (IOException e) {
                emitters.remove(email);
                log.error("Error sending order update to {}: {}", email, e.getMessage());
            }
        }
    }
} 