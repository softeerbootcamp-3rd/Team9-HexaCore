package com.hexacore.tayo.notification.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SseEmitterService {

    // 기본 타임아웃 설정
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final SseEmitterRepository sseEmitterRepository;

    /**
     * 클라이언트가 서버를 구독
     *
     * @param userId 구독하는 클라이언트의 사용자 아이디
     * @return SseEmitter 서버에서 보낸 이벤트 Emitter
     */
    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = createEmitter(userId);

        sendToClient(userId, "Connected");
        return emitter;
    }

    /**
     * 클라이언트에게 데이터를 전송
     *
     * @param id 데이터를 받을 사용자의 아이디
     * @param data 전송할 데이터
     */
    public void sendToClient(Long id, Object data) {
        SseEmitter emitter = sseEmitterRepository.get(id);
        if (emitter != null) {
            try {
                emitter.send(
                        SseEmitter
                                .event()
                                .id(String.valueOf(id))
                                .name("message")
                                .data(data)
                );
            } catch (IOException e) {
                sseEmitterRepository.deleteById(id);
                emitter.completeWithError(e);
            }
        }
    }

    /**
     * 현재 구독중인 모든 사용자에게 알림 전송
     *
     * @param data 전송할 데이터
     */
    public void notifyAll(Object data){
        for (Long emitterKey : sseEmitterRepository.getAllEmitterKeys()) {
            sendToClient(emitterKey, data);
        }
    }

    /**
     * 사용자 아이디를 기반으로 이벤트 Emitter 를 생성
     *
     * @param id 사용자 아이디
     * @return SseEmitter - 생성된 이벤트 Emitter
     */
    private SseEmitter createEmitter(Long id) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        sseEmitterRepository.save(id, emitter);

        // Emitter 가 완료될 때 (모든 데이터가 성공적으로 전송된 상태) Emitter 를 삭제한다.
        emitter.onCompletion(() -> sseEmitterRepository.deleteById(id));
        // Emitter 가 타임아웃 되었을 때 (지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때) Emitter 를 삭제한다.
        emitter.onTimeout(() -> sseEmitterRepository.deleteById(id));

        return emitter;
    }
}
