package com.example.Order.dto;

import lombok.Data;

@Data
public class NotificationRequest {
    private String endpoint;
    private String p256dh;
    private String auth;
    private String payload;
}
