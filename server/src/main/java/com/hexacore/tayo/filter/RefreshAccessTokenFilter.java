package com.hexacore.tayo.filter;


import com.hexacore.tayo.common.errors.AuthException;
import com.hexacore.tayo.common.errors.ErrorCode;
import com.hexacore.tayo.jwt.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor
public class RefreshAccessTokenFilter implements Filter {
    private final JwtService jwtService;

    @Value("${jwt.refresh.cookie-name}")
    private String refreshTokenCookieName;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String clientRefreshToken = getClientRefreshToken(httpServletRequest);
        Claims clientClaims = jwtService.getClaims(clientRefreshToken);

        String serverRefreshToken = jwtService.getRefreshToken(Long.valueOf(clientClaims.get("userId", String.class)));
        Claims serverClaims = jwtService.getClaims(serverRefreshToken);

        // todo 리프레시 토큰 만료 여부를 확인

        // 요청으로 보낸 리프레시 토큰과, 디비에 저장해 놓은 리프레시 토큰 값이 같은지 비교
        if (clientRefreshToken.equals(serverRefreshToken)) {
            // todo 엑세스 재발급
            chain.doFilter(request, response);
        } else {
            throw new AuthException(ErrorCode.INVALID_JWT_TOKEN);
        }
    }

    private String getClientRefreshToken(HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(refreshTokenCookieName))
                    return cookie.getValue();
            }
        }
        // 요청 헤더의 쿠키에 엑세스 토큰이 없는 경우 예외 발생
        throw new AuthException(ErrorCode.INVALID_JWT_TOKEN);
    }
}
