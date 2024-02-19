package com.hexacore.tayo.notification.model;

import com.hexacore.tayo.common.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@RequiredArgsConstructor
@Table(name = "notification")
public class Notification extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "notification_type")
    private NotificationType notificationType;

    @Column(name = "message")
    private String message;

    @Column(name = "is_read")
    private boolean isRead = false;

}
