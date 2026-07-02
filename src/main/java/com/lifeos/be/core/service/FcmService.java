package com.lifeos.be.core.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@Slf4j
public class FcmService {

    @Value("${app.firebase.config-path}")
    private String firebaseConfigPath;

    private final ResourceLoader resourceLoader;
    private boolean isFirebaseInitialized = false;

    public FcmService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void initialize() {
        try {
            log.info("Initializing Firebase Admin SDK using config path: {}", firebaseConfigPath);
            Resource resource = resourceLoader.getResource(firebaseConfigPath);
            
            if (!resource.exists()) {
                log.warn("Firebase configuration file serviceAccountKey.json not found. Push notifications will be simulated.");
                return;
            }

            try (InputStream serviceAccount = resource.getInputStream()) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    FirebaseApp.initializeApp(options);
                }
                isFirebaseInitialized = true;
                log.info("Firebase Admin SDK initialized successfully.");
            }
        } catch (Exception e) {
            log.error("Failed to initialize Firebase Admin SDK: {}. Continuing in simulation mode.", e.getMessage());
        }
    }

    public void sendPushNotification(String fcmToken, String title, String body) {
        if (!isFirebaseInitialized) {
            log.info("[SIMULATION] Sending Push Notification to token '{}': [{}] - {}", fcmToken, title, body);
            return;
        }

        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent FCM message: {}", response);
        } catch (Exception e) {
            log.error("Failed to send FCM message to token '{}': {}", fcmToken, e.getMessage());
        }
    }
}
