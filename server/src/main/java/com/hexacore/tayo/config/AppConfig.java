package com.hexacore.tayo.config;

import com.hexacore.tayo.notification.NotificationService;
import com.hexacore.tayo.notification.manager.InMemoryNotificationManager;
import com.hexacore.tayo.notification.manager.NotificationManager;
import com.hexacore.tayo.notification.sse.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    private final SseEmitterService sseEmitterService;
    private final NotificationService notificationService;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public NotificationManager notificationManager() {
        return new InMemoryNotificationManager(sseEmitterService, notificationService);
    }
}
