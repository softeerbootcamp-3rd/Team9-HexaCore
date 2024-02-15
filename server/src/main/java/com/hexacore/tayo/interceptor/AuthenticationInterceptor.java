package com.hexacore.tayo.interceptor;

import com.hexacore.tayo.auth.jwt.util.JwtParser;
import com.hexacore.tayo.common.UriPath;
import com.hexacore.tayo.util.RequestParser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final JwtParser jwtParser;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access.cookie-name}")
    private String accessTokenCookieName;

    private static final String USER_ID = "userId";
    private static final String USER_NAME = "userName";
    private static final String HTTP_GET = "GET";

    private static final String[] whiteUrlList = {"/cars/[0-9]*"};

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
        String httpMethod = request.getMethod();
        String url = request.getRequestURI();
        if (handler.getClass().equals(ResourceHttpRequestHandler.class) ||
                (httpMethod.equals(HTTP_GET) && canPass(url))
        ) {
            return true;
        }

        String accessToken = RequestParser.getAuthorizationToken(request);
        Claims claims = jwtParser.getClaims(accessToken);

        request.setAttribute(USER_ID, Long.valueOf((Integer) claims.get(USER_ID)));
        request.setAttribute(USER_NAME, claims.get(USER_NAME));
        return true;
    }

    private boolean canPass(String url) {
        for (String possibleUrl : whiteUrlList) {
            Pattern pattern = Pattern.compile(UriPath.PREFIX + possibleUrl);
            Matcher matcher = pattern.matcher(url);

            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }
}
