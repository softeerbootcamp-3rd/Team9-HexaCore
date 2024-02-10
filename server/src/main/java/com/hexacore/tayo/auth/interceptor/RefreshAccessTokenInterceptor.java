package com.hexacore.tayo.auth.interceptor;

import com.hexacore.tayo.common.errors.AuthException;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.auth.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Date;

@RequiredArgsConstructor
public class RefreshAccessTokenInterceptor implements HandlerInterceptor {
    private final JwtService jwtService;

    @Value("${jwt.refresh.cookie-name}")
    private String refreshTokenCookieName;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println("in RefreshAccessTokenInterceptor");
        String clientRefreshToken = RequestParser.getToken(request, refreshTokenCookieName);
        Claims clientClaims = jwtService.getClaims(clientRefreshToken);

        // 리프레시 토큰 만료 여부를 확인
        if (clientClaims.getExpiration().before(new Date())) {
            throw new AuthException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        String serverRefreshToken = jwtService.getRefreshToken(Long.valueOf(clientClaims.get("userId", String.class)));
        // 디비에 저장되어 있는 토큰의 유효성 검증
        jwtService.checkTokenValidation(serverRefreshToken);

        // 클라이언트로부터 받은 리프레시 토큰과, 디비에 저장해 놓은 리프레시 토큰 값이 같은지 비교
        if (clientRefreshToken.equals(serverRefreshToken)) {
            return true;
        } else {
            throw new AuthException(ErrorCode.INVALID_JWT_TOKEN);
        }
    }
}
