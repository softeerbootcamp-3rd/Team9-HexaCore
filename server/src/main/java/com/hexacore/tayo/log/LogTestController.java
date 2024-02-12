package com.hexacore.tayo.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
public class LogTestController {

    Logger asyncLogger = LoggerFactory.getLogger(LogTestController.class);
    Logger logger = LoggerFactory.getLogger("logger");

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        for (int i = 0; i < 50; i++) {
            asyncLogger.info("[+] 비동기 로깅 출력: {}", i);
        }
        logger.debug("[+] 중간에 동기 로깅 출력");
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
