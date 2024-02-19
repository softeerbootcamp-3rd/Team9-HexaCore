package com.hexacore.tayo.notification.dto;

import com.hexacore.tayo.notification.model.Notification;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GetNotificationsResponseDto {

    private List<GetNotificationResponseDto> notifications;

    @Getter
    @Builder
    public static class GetNotificationResponseDto {
        private Long id;
        private String title;
        private String message;
    }

    public static GetNotificationResponseDto of(Notification notification) {
        return GetNotificationResponseDto.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .title(notification.getNotificationType().typeTitle)
                .build();
    }

    public static GetNotificationsResponseDto listOf(List<Notification> notifications) {
        return new GetNotificationsResponseDto(
                notifications
                        .stream()
                        .map(GetNotificationsResponseDto::of)
                        .collect(Collectors.toList())
        );
    }

}
