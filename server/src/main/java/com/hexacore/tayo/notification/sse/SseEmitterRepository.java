package com.hexacore.tayo.notification.sse;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SseEmitterRepository {

    // 모든 Emitter 를 저장하는 ConcurrentHashMap
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 주어진 아이디와 Emitter 저장
     *
     * @param id      - 사용자 아이디
     * @param emitter - 이벤트 Emitter
     */
    public void save(Long id, SseEmitter emitter) {
        emitters.put(id, emitter);
    }

    /**
     * 주어진 아이디의 Emitter 를 제거
     *
     * @param id - 사용자 아이디.
     */
    public void deleteById(Long id) {
        emitters.remove(id);
    }

    /**
     * 주어진 아이디의 Emitter 를 가져옴.
     *
     * @param id 사용자 아이디.
     * @return 이벤트 Emitter.
     */
    public SseEmitter get(Long id) {
        return emitters.get(id);
    }

    public List<SseEmitter> getAllEmitters() {
        return new ArrayList<>(emitters.values());
    }

}
