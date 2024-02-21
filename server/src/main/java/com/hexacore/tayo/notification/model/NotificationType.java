package com.hexacore.tayo.notification.model;

public enum NotificationType {
    CANCEL("예약 취소", " 님이 차량 예약을 취소하였습니다."), // 사용자가 예약을 취소 -> 호스트
    RESERVE("예약 완료", " 님이 차량 예약을 완료하였습니다."), // 사용자가 예약한 경우 -> 호스트
    REFUSE("예약 거절", " 님이 차량 예약을 거절하였습니다."); // 호스트가 사용자의 예약을 거절 -> 사용자

    public final String title;
    public final String msg;

    NotificationType(String title, String msg) {
        this.title = title;
        this.msg = msg;
    }
}
