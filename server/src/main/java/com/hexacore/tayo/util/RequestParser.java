package com.hexacore.tayo.util;

import com.hexacore.tayo.common.errors.AuthException;
import com.hexacore.tayo.common.errors.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class RequestParser {

    /**
     * 요청 헤더의 쿠키에서 토큰을 읽어 반환
     *
     * @param request 요청
     * @return 토큰
     */
    public static String getToken(HttpServletRequest request, String tokenName) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(tokenName))
                    return cookie.getValue();
            }
        }
        // 요청 헤더의 쿠키에 토큰이 없는 경우 예외 발생
        throw new AuthException(ErrorCode.INVALID_JWT_TOKEN);
    }

    /**
     * 요청 헤더 Authorization 에서 토큰을 읽어 반환
     *
     * @param request 요청
     * @return 토큰
     */
    public static String getAuthorizationToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // 요청 헤더 Authorization 에 정상적인 토큰이 없는 경우
            throw new AuthException(ErrorCode.INVALID_JWT_TOKEN);
        }

        // Bearer 제외한 값 리턴
        return authorizationHeader.substring("Bearer ".length());
    }
}
