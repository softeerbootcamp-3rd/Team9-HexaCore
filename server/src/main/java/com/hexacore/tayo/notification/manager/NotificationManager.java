package com.hexacore.tayo.notification.manager;

import com.hexacore.tayo.notification.model.NotificationType;

public interface NotificationManager {

    void notify(Long recipientId, String senderName, NotificationType notificationType);

}
