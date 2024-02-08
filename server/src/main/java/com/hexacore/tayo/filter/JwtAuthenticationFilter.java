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
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor
public class JwtAuthenticationFilter implements Filter {
    private final JwtService jwtService;

    @Value("${jwt.access.cookie-name}")
    private String accessTokenCookieName;

    // 사용자 인증을 거치지 않아도 호출 가능한 api 리스트
    private final String[] whiteURIs = new String[]{"/auth/login", "/auth/refresh", "/auth/signup"};

    /**
     * 모든 요청을 서블릿 컨테이너에 넘기기 전에 가로채 로그인한 사용자인지 인증
     *
     * @param request  요청
     * @param response 요청에 대한 응답
     * @param chain    이후 처리해야 하는, 필터 체인 내의 필터들에 엑세스를 제공
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        // 로그인이 필요없는 api 호출인 경우 검증을 거치지 않고 진행
        if (isWhiteURI(httpServletRequest.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        String accessToken = getAccessToken(httpServletRequest);
        Claims claims = jwtService.getClaims(accessToken);

        // 엑세스 토큰의 만료여부를 확인
        if (claims.getExpiration().after(new Date())) { // 만료되지 않은 토큰인 경우
            request.setAttribute("userId", claims.get("userId"));
            request.setAttribute("userName", claims.get("userName"));
            chain.doFilter(request, response);
        } else { // 만료된 토큰인 경우
            throw new AuthException(ErrorCode.EXPIRED_ACCESS_TOKEN);
        }
    }

    /**
     * 요청 헤더의 쿠키를 가져와 엑세스 토큰을 읽어 반환
     *
     * @param httpServletRequest 요청
     * @return String 토큰
     */
    private String getAccessToken(HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(accessTokenCookieName))
                    return cookie.getValue();
            }
        }
        // 요청 헤더의 쿠키에 엑세스 토큰이 없는 경우 예외 발생
        throw new AuthException(ErrorCode.INVALID_JWT_TOKEN);
    }

    private boolean isWhiteURI(String uri) {
        return PatternMatchUtils.simpleMatch(whiteURIs, uri);
    }
}
