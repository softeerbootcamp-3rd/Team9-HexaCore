package com.hexacore.tayo.notification;

import com.hexacore.tayo.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByReceiverIdOrderByCreatedAtDesc(Long userId);

    void deleteAllByReceiverId(Long userId);

}
