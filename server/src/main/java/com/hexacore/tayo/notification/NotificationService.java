package com.hexacore.tayo.notification;

import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.common.errors.GeneralException;
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
     *
     * @param notification 알림
     */
    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    /**
     * 특정 유저가 받은 모든 알람을 조회합니다.
     *
     * @param userId 수신자 id
     * @return GetNotificationResponseDto
     */
    public List<SseNotificationDto> findAll(Long userId) {
        List<Notification> notifications = notificationRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        return SseNotificationDto.listOf(notifications);
    }

    /**
     * 특정 알림을 모두 삭제합니다.
     *
     * @param userId         삭제 요청한 유저 id
     * @param notificationId 삭제하고자 하는 알림 id
     */
    @Transactional
    public void delete(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() ->
                new GeneralException(ErrorCode.NOTIFICATION_NOT_FOUND));

        if (!notification.getUserId().equals(userId)) {
            throw new GeneralException(ErrorCode.NOTIFICATION_CANNOT_DELETED);
        }

        notificationRepository.delete(notification);
    }

    /**
     * 특정 유저가 받은 알림을 모두 삭제합니다.
     *
     * @param userId 수신자 id
     */
    @Transactional
    public void deleteAll(Long userId) {
        notificationRepository.deleteAllByUserId(userId);
    }
}
