package com.hexacore.tayo.interceptor;

import com.hexacore.tayo.auth.jwt.util.JwtParser;
import com.hexacore.tayo.util.RequestParser;
import com.hexacore.tayo.common.errors.AuthException;
import com.hexacore.tayo.common.errors.ErrorCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.Date;

@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final JwtParser jwtParser;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access.cookie-name}")
    private String accessTokenCookieName;

    /**
     * @param request  HTTP 요청
     * @param response HTTP 응답
     * @param handler  요청에 매핑된 핸들러
     * @return 요청을 핸들러로 매핑된 핸들러로 넘길지 여부 (true: 넘김, false: 안넘김)
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler.getClass().equals(ResourceHttpRequestHandler.class)) {
            return true;
        }

        String accessToken = RequestParser.getToken(request, accessTokenCookieName);
        Claims claims = jwtParser.getClaims(accessToken);

        request.setAttribute("userId", Long.valueOf((Integer) claims.get("userId")));
        request.setAttribute("userName", claims.get("userName"));
        return true;
    }
}
