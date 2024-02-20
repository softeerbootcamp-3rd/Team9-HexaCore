package com.hexacore.tayo.notification.dto;

import com.hexacore.tayo.notification.model.Notification;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class SseNotificationDto {

    private Long id;
    private String title;
    private String message;

    public static SseNotificationDto of(Notification notification) {
        return SseNotificationDto.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .title(notification.getTitle())
                .build();
    }

    public static List<SseNotificationDto> listOf(List<Notification> notifications) {
        return notifications
                .stream()
                .map(SseNotificationDto::of)
                .collect(Collectors.toList());
    }

}
