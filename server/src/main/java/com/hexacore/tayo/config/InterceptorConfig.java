package com.hexacore.tayo.config;

import com.hexacore.tayo.auth.interceptor.AuthenticationInterceptor;
import com.hexacore.tayo.auth.interceptor.RefreshAccessTokenInterceptor;
import com.hexacore.tayo.auth.JwtService;
import com.hexacore.tayo.log.LoggingAspect;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {
    private final JwtService jwtService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 요청에 대한 로깅
        registry.addInterceptor(loggingAspect())
                .order(1);

        registry.addInterceptor(authenticationInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/signup", "/login", "/refresh"); // 로그인 인증 필요없는 요청

        // 엑세스 토큰 재발급 요청인 경우
        registry.addInterceptor(refreshAccessTokenInterceptor())
                .order(3)
                .addPathPatterns("/refresh");
    }

    @Bean
    public RefreshAccessTokenInterceptor refreshAccessTokenInterceptor() {
        return new RefreshAccessTokenInterceptor(jwtService);
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor(jwtService);
    }

    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }
}
