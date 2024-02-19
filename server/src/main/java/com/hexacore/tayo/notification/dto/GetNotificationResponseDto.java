package com.hexacore.tayo.notification.dto;

import com.hexacore.tayo.notification.model.Notification;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GetNotificationResponseDto {

    private List<NotificationDto> notifications;

    @Getter
    @Builder
    public static class NotificationDto {
        private Long id;
        private String title;
        private String message;
    }

    public static NotificationDto of(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .title(notification.getNotificationType().typeTitle)
                .build();
    }

    public static GetNotificationResponseDto listOf(List<Notification> notifications) {
        return new GetNotificationResponseDto(
                notifications
                        .stream()
                        .map(GetNotificationResponseDto::of)
                        .collect(Collectors.toList())
        );
    }

}
