package com.hexacore.tayo.notification.model;

public enum NotificationType {
    CANCEL("예약 취소"), // 사용자가 예약을 취소 -> 호스트
    RESERVE("예약 완료"), // 사용자가 예약한 경우 -> 호스트
    REFUSE("예약 거절"); // 호스트가 사용자의 예약을 거절 -> 사용자

    public final String typeTitle;

    NotificationType(String typeTitle) {
        this.typeTitle = typeTitle;
    }
}
