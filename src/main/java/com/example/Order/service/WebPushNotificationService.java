package com.example.Order.service;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutionException;

@Service
public class WebPushNotificationService {

    private static final String PUBLIC_KEY = "YOUR_PUBLIC_KEY";
    private static final String PRIVATE_KEY = "YOUR_PRIVATE_KEY";

    public void sendPushNotification(String endpoint, String auth, String p256dh, String payload) throws GeneralSecurityException, JoseException, IOException, ExecutionException, InterruptedException {
        PushService pushService = new PushService(PUBLIC_KEY, PRIVATE_KEY);
        Notification notification = new Notification(endpoint, p256dh, auth, payload);
        pushService.send(notification);
    }
}
