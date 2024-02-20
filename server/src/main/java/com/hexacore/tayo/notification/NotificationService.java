package com.hexacore.tayo.notification;

import com.hexacore.tayo.notification.dto.SseNotificationDto;
import com.hexacore.tayo.notification.model.Notification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 알림을 저장합니다.
     * @param notification 알림
     */
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    /**
     * 특정 유저가 받은 모든 알람을 조회합니다.
     * @param userId 수신자 id
     * @return GetNotificationResponseDto
     */
    public List<SseNotificationDto> findAll(Long userId) {
        List<Notification> notifications = notificationRepository.findAllByUserId(userId);

        return SseNotificationDto.listOf(notifications);
    }

    /**
     * 특정 유저가 받은 모든 알람을 조회하고 읽음 여부를 true 로 업데아트 합니다.
     * @param userId 수신자 id
     * @return GetNotificationResponseDto
     */
    @Transactional
    public List<SseNotificationDto> findAllAndUpdateReadTrue(Long userId) {
        List<Notification> arr = notificationRepository.findAllByUserId(userId);
        for (Notification notification : arr) {
            notification.setRead(true);
        }

        return SseNotificationDto.listOf(arr);
    }

    /**
     * 특정 유저가 받은 알림을 모두 삭제합니다.
     * @param userId 수신자 id
     */
    @Transactional
    public void deleteAll(Long userId) {
        notificationRepository.deleteAllByUserId(userId);
    }
}
