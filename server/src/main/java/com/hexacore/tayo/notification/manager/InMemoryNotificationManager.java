package com.hexacore.tayo.notification.manager;

import com.hexacore.tayo.notification.NotificationService;
import com.hexacore.tayo.notification.dto.SseNotificationDto;
import com.hexacore.tayo.notification.model.Notification;
import com.hexacore.tayo.notification.model.NotificationType;
import com.hexacore.tayo.notification.sse.SseEmitterService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InMemoryNotificationManager implements NotificationManager {

    private final SseEmitterService sseEmitterService;
    private final NotificationService notificationService;

    // 알림 전송
    public void notify(Long recipientId, String senderName, NotificationType notificationType) {
        // 알림을 저장소에 저장
        Notification notification = notificationService.save(Notification.builder()
                .receiverId(recipientId)
                .title(notificationType.title)
                .message(senderName + notificationType.msg)
                .build()
        );

        // 클라이언트에 해당 알림을 SSE 이벤트 전송
        sseEmitterService.sendToClient(recipientId, SseNotificationDto.of(notification));
    }

}
