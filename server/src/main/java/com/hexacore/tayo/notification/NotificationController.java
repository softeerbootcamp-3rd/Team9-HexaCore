package com.hexacore.tayo.notification;

import com.hexacore.tayo.common.response.Response;
import com.hexacore.tayo.notification.dto.SendNotificationRequestDto;
import com.hexacore.tayo.notification.dto.SseNotificationDto;
import com.hexacore.tayo.notification.sse.SseEmitterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final SseEmitterService sseEmitterService;
    private final NotificationService notificationService;

    /**
     *
     * @param request 사용자 요청
     * @return SseEmitter
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        return sseEmitterService.subscribe(userId);
    }

    /**
     * 알림 전송 테스트 api
     * @param id 수신자 id
     * @param notification 알림
     */
    @PostMapping("/{id}")
    public ResponseEntity<Response> sendData(@PathVariable Long id, @RequestBody SendNotificationRequestDto notification) {
        sseEmitterService.sendToClient(id, notification);

        return Response.of(HttpStatus.OK);
    }

    /**
     * 모든 사용자에게 알림을 보냅니다.
     * @param notification 알림
     */
    @PostMapping
    public void sendNotification(@RequestBody SendNotificationRequestDto notification) {
        sseEmitterService.notifyAll(notification);
    }

    /**
     * 지금까지 전송된, 모든 알림을 조회합니다.
     * @param request
     * @return
     */
    @GetMapping
    public ResponseEntity<Response> getNotifications(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<SseNotificationDto> response = notificationService.findAll(userId);

        return Response.of(HttpStatus.OK, response);
    }

    /**
     * 유저가 삭제 요청한 알림을 삭제합니다.
     * @param request
     * @return
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Response> deleteNotification(HttpServletRequest request, @PathVariable Long notificationId) {
        Long userId = (Long) request.getAttribute("userId");
        notificationService.delete(userId, notificationId);

        return Response.of(HttpStatus.OK);
    }

    /**
     * 유저의 알림을 모두 삭제합니다.
     * @param request
     * @return
     */
    @DeleteMapping
    public ResponseEntity<Response> deleteAllNotifications(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        notificationService.deleteAll(userId);

        return Response.of(HttpStatus.OK);
    }
}
