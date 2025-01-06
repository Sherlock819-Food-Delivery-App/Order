package com.example.Order.controller;

import com.example.Order.dto.NotificationRequest;
import com.example.Order.service.WebPushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/push")
public class PushNotificationController {

    @Autowired
    private WebPushNotificationService pushService;

    @PostMapping("/send")
    public String sendNotification(@RequestBody NotificationRequest request) {
        try {
            pushService.sendPushNotification(
                    request.getEndpoint(),
                    request.getAuth(),
                    request.getP256dh(),
                    request.getPayload()
            );
            return "Notification sent successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to send notification";
        }
    }
}
