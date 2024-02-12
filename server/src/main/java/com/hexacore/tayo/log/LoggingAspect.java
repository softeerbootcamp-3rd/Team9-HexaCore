package com.hexacore.tayo.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect implements HandlerInterceptor {

    // Auth 성공 로깅
    @AfterReturning(pointcut = "execution(* com.hexacore.tayo.auth.JwtService.*(..))", returning = "result")
    public void logAuthSuccess(JoinPoint joinPoint, Object result) {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        logger.info("[+] AUTH - Request: {}, Result: {}", joinPoint.getSignature().toShortString(), result);
    }

    // 예외 로깅
    @AfterThrowing(pointcut = "execution(* com.hexacore.tayo..*.*(..))", throwing = "ex")
    public void logError(JoinPoint joinPoint, Throwable ex) {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());

        // 현재 클래스의 파일 이름과 라인 번호 가져오기
        StackTraceElement[] stackTraceElements = ex.getStackTrace();
        String fileName = stackTraceElements[0].getFileName();
        int lineNumber = stackTraceElements[0].getLineNumber();

        // 예외 메시지 가져오기
        String errorMessage = ex.getMessage();

        logger.error("[-] ERROR - File: {}, Line: {}, Method: {}, Error Message: {}" ,fileName, lineNumber,
                joinPoint.getSignature().toShortString() ,errorMessage);
    }

    // 들어온 요청 로깅
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Logger logger = LoggerFactory.getLogger(request.getClass());

        logger.info("[+] REQUEST - Method: {}, URL: {}, RemoteAddr: {}",
                request.getMethod(), request.getRequestURI(), request.getRemoteAddr());

        return true; // 계속 요청을 처리하도록 true 반환
    }

    // DB 로깅
    @AfterReturning(pointcut = "execution(* com.hexacore.tayo..*Repository.*(..))", returning = "result")
    public void logDBOperation(JoinPoint joinPoint, Object result) {
        // Spring Data Jpa에서 JpaRepository를 구현한 클래스인 SimpleJpaRepository가 프록시 객체를 감싸고 있음
        // 프록시 객체가 실제로 어떤 인터페이스를 구현하고 있는지 확인
        Class<?>[] interfaces = joinPoint.getTarget().getClass().getInterfaces();
        if (interfaces.length > 0) {
            // 인터페이스가 존재하는 경우, 첫 번째 인터페이스의 이름을 가져옴
            String interfaceName = interfaces[0].getName();
            Logger logger = LoggerFactory.getLogger(interfaceName);

            // 현재 실행 중인 메서드의 이름 가져오기
            String methodName = joinPoint.getSignature().getName();

            // 로그 출력
            logger.info("[+] DB - operation: {}.{}", interfaceName, methodName);
            logger.info("[+] DB - result : {}", result);
        } else {
            // 프록시가 인터페이스를 구현하지 않는 경우, 프록시 클래스의 이름 사용
            String proxyClassName = joinPoint.getTarget().getClass().getName();
            Logger logger = LoggerFactory.getLogger(proxyClassName);

            // 현재 실행 중인 메서드의 이름 가져오기
            String methodName = joinPoint.getSignature().getName();

            // 로그 출력
            logger.info("[+] DB - operation: {}.{}", proxyClassName, methodName);
            logger.info("[+] DB - result : {}", result);
        }
    }

    // 컨트롤러 성공 로깅
    @AfterReturning(pointcut = "execution(* com.hexacore.tayo..*Controller.*(..))", returning = "result")
    public void logControllerSuccess(JoinPoint joinPoint, Object result) {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        logger.info("[+] CONTROLLER - Request: {}, Result: {}", joinPoint.getSignature().toShortString(), result);
    }
}
