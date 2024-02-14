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

import java.util.Date;

@RequiredArgsConstructor
public class RefreshAccessTokenInterceptor implements HandlerInterceptor {

    private final JwtParser jwtParser;

    @Value("${jwt.refresh.cookie-name}")
    private String refreshTokenCookieName;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String clientRefreshToken = RequestParser.getToken(request, refreshTokenCookieName);
        Claims clientClaims = jwtParser.getClaims(clientRefreshToken);

        // userId로 서버에 저장된 리프레시 토큰 조회 후 검증
        Long userId = Long.valueOf(clientClaims.getSubject());
        String serverRefreshToken = jwtParser.getValidRefreshToken(userId);

        // 클라이언트로부터 받은 리프레시 토큰과, 디비에 저장해 놓은 리프레시 토큰 값이 같은지 비교
        if (clientRefreshToken.equals(serverRefreshToken)) {
            request.setAttribute("userId", userId);
            return true;
        } else {
            throw new AuthException(ErrorCode.INVALID_JWT_TOKEN);
        }
    }
}
